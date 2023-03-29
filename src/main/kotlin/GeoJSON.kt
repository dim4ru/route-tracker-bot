import kotlinx.serialization.Serializable
@Serializable
data class GeoJSON(
    val type: String = "LineString",
    val coordinates: List<List<Double>>
)