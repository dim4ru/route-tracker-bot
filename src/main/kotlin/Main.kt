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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


const val TOKEN = "6043309402:AAGwbvNXC6g_ulQbis1fTGglWpLHAkMENWU"
val bot = telegramBot(TOKEN)
var startTime: String = ""
suspend fun main() {
    bot.buildBehaviourWithLongPolling {
        println(getMe())

        onCommand("start") {
            reply(it, "Привет, я могу следить за тобой! Отправь мне трансляцию геопозиции.")
        }
        onLiveLocation {
            startTime = getCurrentISOTime()    // лучше DateTime -> ISO-8601
            saveLocation(it.location as LiveLocation)
        }
        onEditedLocation {
            val liveLocation = it.location as LiveLocation
            saveLocation(liveLocation)
        }
        onEditedContentMessage {
            val lat = it.location!!.latitude
            val long = it.location!!.longitude
            if (it.content is StaticLocationContent) {
                finishTracking()
            }
            reply(it, "$lat,$long")
            bot.sendPhoto(
                IdChatIdentifier(it.chat.id.chatId),
                InputFile.fromUrl("https://static.maps.2gis.com/1.0?s=1200x1200@2x&pt=$lat,$long~n:1&pt=$lat,$long~k:c~n:2\n")
            )
        }
        allUpdatesFlow.subscribe (this) { println(it) }
    }.join()

}

fun getCurrentISOTime(): String {
    val currentDateTime = LocalDateTime.now()
    val isoDateTime = currentDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    return isoDateTime!!
}

fun saveLocation(location: LiveLocation) {
    println(getCurrentISOTime() + " ${location.longitude}, ${location.latitude}")
}

fun finishTracking() {
    println("tracking finished")
}