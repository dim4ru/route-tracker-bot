import kotlinx.serialization.Serializable

@Serializable
data class GeoPoint(
    val lat: Double,
    val long: Double
)