@file:OptIn(RiskFeature::class)

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.edit.media.editMessageMedia
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onEditedContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onLiveLocation
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.location
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.ChatIdentifier
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.MessageId
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
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
        var geoJSON = GeoJSON()
        //val geoPoints: MutableList<List<Double>> = mutableListOf() // long, lat
        var statisticsMessageId: MessageId? = null
        var mapMessage: ContentMessage<PhotoContent>? = null
        println(getMe())

        onCommand("start") {
            reply(it, "Привет, я могу следить за тобой! Отправь мне трансляцию геопозиции.")
        }
        // On live location start
        onLiveLocation {
            statisticsMessageId = bot.sendTextMessage(it.chat.id, printRouteDistance(geoJSON.geoPoints)).messageId
            lat = it.location!!.latitude
            long = it.location!!.longitude
            println("Tracking started")
            saveLocation(geoJSON.geoPoints, lat!!, long!!)
            mapMessage = bot.sendPhoto(
                IdChatIdentifier(it.chat.id.chatId),
                InputFile.fromUrl(getRouteMapURL(geoJSON))
            )

        }
        // On live location update and ending (expiration and stop by user)
        onEditedContentMessage {
            lat = it.location!!.latitude
            long = it.location!!.longitude
            // If life location is active
            if (it.content !is StaticLocationContent) {
                saveLocation(geoJSON.geoPoints, lat!!, long!!)
                if (statisticsMessageId != null) {
                    updateStatisticsMessage(it.chat.id, statisticsMessageId, geoJSON.geoPoints)
                }
                if (geoJSON.geoPoints.size % 10 == 0) {
                    updateRouteMap(mapMessage!!, geoJSON)
                }
            } else {
                // End tracking
                updateRouteMap(mapMessage!!, geoJSON)
                geoJSON.geoPoints = mutableListOf()
                statisticsMessageId = null
            }
        }
        // updateRouteMap(mapMessage!!, geoJSON) <--?

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
    chatId: ChatIdentifier,
    messageId: MessageId?,
    geoPoints: MutableList<List<Double>>
) {
    bot.edit(chatId, messageId!!, printRouteDistance(geoPoints))
    println("Distance updated: " + String.format("%.3f", getRouteDistance(geoPoints)))
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
    println("Total distance: $routeDistance")
    return routeDistance
}

fun getRouteMapURL(geoJSON: GeoJSON): String {
    val encodedURL = "https://static.maps.2gis.com/1.0?s=880x450@2x&z=&g=${URLEncoder.encode(geoJSON.serializeToGeoJSON(), "UTF-8")}"
    println("Map URL: $encodedURL")
    return encodedURL
}

suspend fun updateRouteMap(message: ContentMessage<PhotoContent>, geoJSON: GeoJSON) {
    bot.editMessageMedia(
        message = message,
        media = TelegramMediaPhoto(file = InputFile.fromUrl(getRouteMapURL(geoJSON)))
    )
    println("Map updated")
}