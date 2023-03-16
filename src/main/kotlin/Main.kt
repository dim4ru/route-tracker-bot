import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onLiveLocation

const val TOKEN = "6043309402:AAGwbvNXC6g_ulQbis1fTGglWpLHAkMENWU"
suspend fun main() {
    val bot = telegramBot(TOKEN)

    bot.buildBehaviourWithLongPolling {
        println(getMe())

        onCommand("start") {
            reply(it, "Hi:)")
        }
        onLiveLocation {
            reply(it, "Вижу тебя!")
        }
    }.join()
}