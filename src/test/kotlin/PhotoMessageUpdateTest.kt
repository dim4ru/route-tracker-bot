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
            bot.editMessageMedia(
                message = message,
                media = TelegramMediaPhoto(file = InputFile.fromUrl("https://i1.sndcdn.com/artworks-CyTzk0PMsjHFfr7D-S8wWcw-t500x500.jpg"))
            )
        }
    }
    "Update route map" {
        bot.buildBehaviourWithLongPolling {
            val geoPoints: MutableList<List<Double>> = mutableListOf(
                listOf(73.385541, 54.968875),
                listOf(73.385723, 54.968203),
                listOf(73.390598, 54.968447),
                listOf(73.390904, 54.967077),
                listOf(73.392136, 54.965436),
                listOf(73.396102, 54.965850)
            )
            val message = bot.sendPhoto(
                ChatId(chatId = 509933088),
                InputFile.fromUrl("https://yt3.googleusercontent.com/umigHrIECcGghXOyOb73g0gGPbV164TDoL5xQ5j8RE-O84eXpHW8SDn3eBZDSiloZWnNRU5Br4Q=s900-c-k-c0x00ffffff-no-rj")
            )
            updateRouteMap(message = message, GeoJSON(geoPoints = geoPoints), showLast = false)
        }
    }
})