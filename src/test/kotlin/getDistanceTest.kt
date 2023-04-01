import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class GetDistanceTest : StringSpec({
    "Distance between two points calculated with the haversine"{
        Haversine().getDistance(54.968875, 73.385541, 54.968203, 73.385723) shouldBe 0.076.plusOrMinus(0.001)
    }
})