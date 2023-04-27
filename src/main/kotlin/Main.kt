@file:OptIn(RiskFeature::class)

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.edit.caption.editMessageCaption
import dev.inmo.tgbotapi.extensions.api.edit.media.editMessageMedia
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.*
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.location
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.content.LiveLocationContent
import dev.inmo.tgbotapi.types.message.content.PhotoContent
import dev.inmo.tgbotapi.types.message.content.StaticLocationContent
import dev.inmo.tgbotapi.utils.RiskFeature
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import java.io.FileInputStream
import java.io.InputStream
import java.net.URLEncoder
import java.util.*

fun readTelegramBotToken(): String {
    val inputStream: InputStream = FileInputStream("config.properties")
    val prop = Properties().apply { load(inputStream) }
    return prop.getProperty("telegram.bot.token")
}

val TOKEN = readTelegramBotToken()
val bot = telegramBot(TOKEN)

suspend fun main() {
    bot.buildBehaviourWithLongPolling {
        var lat: Double?
        var long: Double?
        val geoJSON = GeoJSON()
        var statisticsMessage: ContentMessage<PhotoContent>? = null
        println(getMe())

        onCommand("start") {
            reply(it, "Привет, я могу следить за тобой! Отправь мне трансляцию геопозиции.")
        }
        // On live location start
        onLiveLocation {
            lat = it.location!!.latitude
            long = it.location!!.longitude
            println("Tracking started")
            saveLocation(geoJSON.geoPoints, lat!!, long!!)
            statisticsMessage = sendPhoto(it.chat.id, InputFile.fromUrl(getRouteMapURL(geoJSON)),"Началась запись маршрута")
        }
        // On live location update and ending (expiration and stop by user)
        onEditedContentMessage {
            lat = it.location!!.latitude
            long = it.location!!.longitude
            // If life location is active
            if (it.content is LiveLocationContent) {
                saveLocation(geoJSON.geoPoints, lat!!, long!!)
                if (statisticsMessage != null) {
                    updateStatisticsMessage(statisticsMessage!!, geoJSON)
                }
            } else {
                // End tracking
                updateStatisticsMessage(statisticsMessage!!, geoJSON, false)
                geoJSON.geoPoints = mutableListOf()
                statisticsMessage = null
            }
        }

        //allUpdatesFlow.subscribe(this) { println(it) }
    }.join()

}

fun getCurrentISOTime(): String {
    val currentDateTime = LocalDateTime.now()
    val isoDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    return isoDateTime!!
}

fun saveLocation(geoPoints: MutableList<List<Double>>, lat: Double, long: Double) {
    geoPoints.add(listOf(long, lat)) // Order is inverse becsuse 2gis API requires coordinates in format longitude, latitude
    println("Added: ${listOf(long, lat)} (${geoPoints.count()})")
}

suspend fun updateStatisticsMessage(
    message: ContentMessage<PhotoContent>,
    geoJSON: GeoJSON,
    showLast: Boolean = true
) {
    println("Distance updated: " + String.format("%.3f", getRouteDistance(geoJSON.geoPoints)))

    // Map update on every 10th point
    if (geoJSON.geoPoints.size % 10 == 0 || !showLast) {
        updateRouteMap(message, geoJSON, showLast)
    }
    bot.editMessageCaption(message.chat.id, message.messageId, printRouteDistance(geoJSON.geoPoints))
}


fun printRouteDistance(geoPoints: MutableList<List<Double>>): String {
    return "Пройденное расстояние: " + String.format("%.3f", getRouteDistance(geoPoints)) + "км"
}

fun getRouteDistance(geoPoints: MutableList<List<Double>>): Double {
    var routeDistance = 0.0
    for (i in 0 until geoPoints.size - 1) {
        routeDistance += Haversine().getDistance(
            geoPoints[i][0],
            geoPoints[i][1],
            geoPoints[i + 1][0],
            geoPoints[i + 1][1]
        )
    }
    return routeDistance
}

fun getRouteMapURL(geoJSON: GeoJSON, showLast: Boolean = true): String {
    val geoJSONString = geoJSON.serializeToGeoJSON().encodeToURL()
    val lastPoint = (geoJSON.geoPoints.last().toList().apply { apply { Collections.swap(this, 0, 1) } }).joinToString(",")

    // If tracking is in process, send map with point, else just a line
    val url = if (showLast) {
        "https://static.maps.2gis.com/1.0?s=880x450@2x&z=&pt=${lastPoint}~k:c~c:be~s:l&g=$geoJSONString"
    } else {
        "https://static.maps.2gis.com/1.0?s=880x450@2x&z=&g=$geoJSONString"
    }
    println("Map URL: $url")
    return url
}

fun String.encodeToURL(): String = URLEncoder.encode(this, "UTF-8")

suspend fun updateRouteMap(message: ContentMessage<PhotoContent>, geoJSON: GeoJSON, showLast: Boolean = true) {
    bot.editMessageMedia(
        message = message,
        media = TelegramMediaPhoto(file = InputFile.fromUrl(getRouteMapURL(geoJSON, showLast)))
    )
    println("Map updated")
}