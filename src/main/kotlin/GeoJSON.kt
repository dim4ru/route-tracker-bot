import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
class GeoJSON(
    @EncodeDefault val type: String = "LineString",
    @SerialName("coordinates") var geoPoints: MutableList<List<Double>> = mutableListOf()
) {
    fun serializeToGeoJSON(): String {
        return kotlinx.serialization.json.Json.encodeToString(this)
    }
    // Unresolved reference when tried to use:
    fun GeoJSON.clearCoordinates() {
        geoPoints = mutableListOf()
    }
}