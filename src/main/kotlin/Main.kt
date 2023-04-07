@file:OptIn(RiskFeature::class)

import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.edit.edit
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
import dev.inmo.tgbotapi.types.message.content.StaticLocationContent
import dev.inmo.tgbotapi.utils.RiskFeature
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kotlinx.serialization.encodeToString
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
    var lat: Double?
    var long: Double?
    bot.buildBehaviourWithLongPolling {
        val geoPoints: MutableList<List<Double>> = mutableListOf() // long, lat
        var statisticsMessageId: MessageId = 0
        println(getMe())

        onCommand("start") {
            reply(it, "Привет, я могу следить за тобой! Отправь мне трансляцию геопозиции.")
        }
        // On live location start
        onLiveLocation {
            lat = it.location!!.latitude
            long = it.location!!.longitude
            println("Tracking started")
            saveLocation(geoPoints, lat!!, long!!)
            statisticsMessageId = bot.sendTextMessage(it.chat.id, printRouteDistance(geoPoints)).messageId
        }
        // On live location update and ending (expiration and stop by user)
        onEditedContentMessage {
            lat = it.location!!.latitude
            long = it.location!!.longitude
            // If life location is active
            if (it.content !is StaticLocationContent) {
                saveLocation(geoPoints, lat!!, long!!)
                updateStatisticsMessage(it.chat.id, statisticsMessageId, geoPoints)
            } else {
                // End tracking
                bot.sendPhoto(
                    IdChatIdentifier(it.chat.id.chatId),
                    InputFile.fromUrl(getRouteMapURL(geoPoints))
                )
                geoPoints.clear()
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

fun getRouteMapURL(geoPoints: MutableList<List<Double>>): String {
    val json = kotlinx.serialization.json.Json.encodeToString(GeoJSON("LineString", geoPoints))
    val encodedURL = "https://static.maps.2gis.com/1.0?s=880x450&z=&g=${URLEncoder.encode(json, "UTF-8")}"
    println("Map URL: $encodedURL")
    return encodedURL
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

suspend fun updateStatisticsMessage(chatId: ChatIdentifier, messageId: MessageId, geoPoints: MutableList<List<Double>>) {
    bot.edit(chatId, messageId, printRouteDistance(geoPoints))
    println("Total distance updated: ${printRouteDistance(geoPoints)}")
}

fun printRouteDistance (geoPoints: MutableList<List<Double>>): String{
    return "Пройденное расстояние: " + String.format("%.3f", getRouteDistance(geoPoints)) + "км"
}