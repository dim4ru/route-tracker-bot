@file:OptIn(RiskFeature::class)

import dev.inmo.micro_utils.coroutines.subscribe
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onEditedContentMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onEditedLocation
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onLiveLocation
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.location
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.location.LiveLocation
import dev.inmo.tgbotapi.types.message.content.StaticLocationContent
import dev.inmo.tgbotapi.utils.RiskFeature
import io.ktor.http.ContentType.Application.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString


const val TOKEN = "6043309402:AAGwbvNXC6g_ulQbis1fTGglWpLHAkMENWU"
val bot = telegramBot(TOKEN)
var geoPoints: MutableList<List<Double>> = mutableListOf()
suspend fun main() {
    bot.buildBehaviourWithLongPolling {
        println(getMe())

        onCommand("start") {
            reply(it, "Привет, я могу следить за тобой! Отправь мне трансляцию геопозиции.")
        }
        // On live location start
        onLiveLocation {
            val lat = it.location!!.latitude
            val long = it.location!!.longitude
            saveLocation(lat, long)
        }
        // On live location update and ending (expiration and stop by user)
        onEditedContentMessage {
            val lat = it.location!!.latitude
            val long = it.location!!.longitude
            // If life location is active
            if (it.content !is StaticLocationContent) {
                saveLocation(lat, long)
            } else {
                finishTracking()
            }
        }

        allUpdatesFlow.subscribe(this) { println(it) }
    }.join()

}

fun getCurrentISOTime(): String {
    val currentDateTime = LocalDateTime.now()
    val isoDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    return isoDateTime!!
}

fun saveLocation(lat: Double, long: Double) {
    geoPoints.add(listOf(lat, long))
}

fun finishTracking() {
    val json = kotlinx.serialization.json.Json.encodeToString(GeoJSON("LineString", geoPoints))
    println(json)
}