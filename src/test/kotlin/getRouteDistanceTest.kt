import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class GetRouteDistanceTest : StringSpec({
    "Total route distance"{
        val geoPoints: MutableList<List<Double>> = mutableListOf(
            listOf(54.968875, 73.385541),
            listOf(54.968203, 73.385723),
            listOf(54.968447, 73.390598),
            listOf(54.967077, 73.390904),
            listOf(54.965436, 73.392136),
            listOf(54.965850, 73.396102),
            listOf(54.965257, 73.396176)
        )
        getRouteDistance(geoPoints) shouldBe 1.0.plusOrMinus(0.1)
    }
})