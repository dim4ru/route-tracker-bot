import kotlinx.serialization.Serializable
@Serializable
data class GeoJSON(
    val type: String,
    val coordinates: List<List<Double>>
)