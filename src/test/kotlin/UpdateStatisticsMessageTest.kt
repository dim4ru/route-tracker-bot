import dev.inmo.tgbotapi.types.ChatId
import io.kotest.core.spec.style.StringSpec

class UpdateStatisticsMessageTest : StringSpec({
    "Editing a message after a location is added" {
        val geoPoints: MutableList<List<Double>> = mutableListOf(
            listOf(54.968875, 73.385541),
            listOf(54.968203, 73.385723),
            listOf(54.968447, 73.390598),
            listOf(54.967077, 73.390904),
            listOf(54.965436, 73.392136),
            listOf(54.965850, 73.396102),
            listOf(54.965257, 73.396176)
        )
        updateStatisticsMessage(ChatId(chatId = 509933088), 980, geoPoints)
    }
})