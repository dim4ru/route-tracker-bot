import dev.inmo.tgbotapi.extensions.api.edit.caption.editMessageCaption
import dev.inmo.tgbotapi.extensions.api.edit.media.editMessageMedia
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.ChatId
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import io.kotest.core.spec.style.StringSpec

class PhotoMessageUpdateTest : StringSpec({
    "Edit photo caption" {
        val id = ChatId(chatId = 509933088)
        bot.buildBehaviourWithLongPolling {
            val message = bot.sendPhoto(
                id,
                InputFile.fromUrl("https://yt3.googleusercontent.com/umigHrIECcGghXOyOb73g0gGPbV164TDoL5xQ5j8RE-O84eXpHW8SDn3eBZDSiloZWnNRU5Br4Q=s900-c-k-c0x00ffffff-no-rj")
            )
            editMessageCaption(id, message.messageId, "caption")
        }
    }
    "Replace photo" {
        val id = ChatId(chatId = 509933088)
        bot.buildBehaviourWithLongPolling {
            val message = bot.sendPhoto(
                id,
                InputFile.fromUrl("https://yt3.googleusercontent.com/umigHrIECcGghXOyOb73g0gGPbV164TDoL5xQ5j8RE-O84eXpHW8SDn3eBZDSiloZWnNRU5Br4Q=s900-c-k-c0x00ffffff-no-rj")
            )
            bot.editMessageMedia(message=message, media = TelegramMediaPhoto(file = InputFile.fromUrl("https://i1.sndcdn.com/artworks-CyTzk0PMsjHFfr7D-S8wWcw-t500x500.jpg")))
        }
    }
})