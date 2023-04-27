import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
class GeoJSON(
    @EncodeDefault val type: String = "LineString",
    var coordinates: MutableList<Coordinates> = mutableListOf()
) {
    fun serializeToGeoJSON(): String {
        return kotlinx.serialization.json.Json.encodeToString(this)
    }
}