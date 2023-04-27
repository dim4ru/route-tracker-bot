import kotlinx.serialization.Serializable

typealias Latitude = Double
typealias Longitude = Double

@Serializable
data class Coordinates(
    val latitude: Latitude?,
    val longitude: Longitude?
)