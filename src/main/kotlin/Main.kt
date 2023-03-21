package main

import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.api.send.*
import dev.inmo.tgbotapi.extensions.api.startLiveLocation
import dev.inmo.tgbotapi.extensions.utils.updates.retrieving.startGettingOfUpdatesByLongPolling
import dev.inmo.tgbotapi.types.chat.*

import dev.inmo.micro_utils.coroutines.subscribeSafelyWithoutExceptions
import dev.inmo.tgbotapi.extensions.api.EditLiveLocationInfo
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.edit.edit
import dev.inmo.tgbotapi.extensions.api.handleLiveLocation
import dev.inmo.tgbotapi.extensions.api.send.*
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitMessageDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.oneOf
import dev.inmo.tgbotapi.extensions.behaviour_builder.parallel
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.*
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.location
import dev.inmo.tgbotapi.extensions.utils.extensions.sameMessage
import dev.inmo.tgbotapi.extensions.utils.formatting.linkMarkdownV2
import dev.inmo.tgbotapi.extensions.utils.formatting.textMentionMarkdownV2
import dev.inmo.tgbotapi.extensions.utils.ifFromChannelGroupContentMessage
import dev.inmo.tgbotapi.extensions.utils.liveLocationOrThrow
import dev.inmo.tgbotapi.extensions.utils.types.buttons.dataButton
import dev.inmo.tgbotapi.extensions.utils.types.buttons.flatInlineKeyboard
import dev.inmo.tgbotapi.requests.edit.media.editMessageMediaMethod
import dev.inmo.tgbotapi.types.chat.*
import dev.inmo.tgbotapi.types.chat.GroupChat
import dev.inmo.tgbotapi.types.chat.PrivateChat
import dev.inmo.tgbotapi.types.chat.SupergroupChat
import dev.inmo.tgbotapi.types.location.LiveLocation
import dev.inmo.tgbotapi.types.location.LocationSerializer
import dev.inmo.tgbotapi.types.locationField
import dev.inmo.tgbotapi.types.longitudeField
import dev.inmo.tgbotapi.types.message.MarkdownV2
import dev.inmo.tgbotapi.types.message.abstracts.CommonMessage
import dev.inmo.tgbotapi.types.message.abstracts.ContentMessage
import dev.inmo.tgbotapi.types.message.abstracts.Message
import dev.inmo.tgbotapi.types.message.content.LiveLocationContent
import dev.inmo.tgbotapi.types.message.content.LiveLocationMessage
import dev.inmo.tgbotapi.types.message.content.LocationContent
import dev.inmo.tgbotapi.utils.PreviewFeature
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.extensions.escapeMarkdownV2Common
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

const val TOKEN = "6043309402:AAGwbvNXC6g_ulQbis1fTGglWpLHAkMENWU"
val bot = telegramBot(TOKEN)
var locations = listOf<Pair<Double, Double>>()

@RiskFeature
suspend fun main() {
    bot.buildBehaviourWithLongPolling {
        println(getMe())

        onCommand("start") {
            reply(it, "Привет, я могу следить за тобой, но пока что эти данные никак не сохраняются.")
        }
        onLiveLocation {
            val coordinates = Pair(it.location!!.latitude, it.location!!.longitude)
            reply(it, "Вижу тебя: $coordinates")
            saveLocation(coordinates)
        }
        onEditedLocation {
            val coordinates = Pair(it.location!!.latitude, it.location!!.longitude)
            reply(it, "Вижу тебя: $coordinates")
            if (!locationExpired(it, coordinates)) {
                saveLocation(coordinates)
            } else {
                viewResults()
            }
        }
    }.join()
}

fun locationExpired(message: Message, coordinates: Pair<Double, Double>): Boolean {
    // проверка истечения трансляции
    return false
}

fun saveLocation(coordinates: Pair<Double, Double>) {
    locations = locations + coordinates
}

fun viewResults() {

}

//liveLocationOrThrow()