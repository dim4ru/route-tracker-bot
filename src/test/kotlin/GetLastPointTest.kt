import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.ChatId
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class GetLastPointTest  : StringSpec({
    "Get last point from geoPoints list" {
        val geoPoints: MutableList<List<Double>> = mutableListOf(
            listOf(54.968875, 73.385541),
            listOf(54.968203, 73.385723),
            listOf(54.965257, 73.396176)
        )
        val lastPoint = geoPoints.last().apply { Collections.swap(this, 0, 1) }
        lastPoint.joinToString(", ") shouldBe "73.396176, 54.965257"
    }
})