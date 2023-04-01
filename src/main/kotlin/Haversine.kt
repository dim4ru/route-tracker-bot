import java.lang.Math.*

class Haversine {
    companion object {
        const val R = 6371.0 // in kilometers
    }

    fun getDistance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val lat1Rad = toRadians(lat1)
        val lat2Rad = toRadians(lat2)
        val deltaLat = toRadians(lat2 - lat1)
        val deltaLong = toRadians(long2 - long1)
        return 2 * Companion.R * asin(sqrt(pow(sin(deltaLat / 2), 2.0) + pow(sin(deltaLong / 2), 2.0) * cos(lat1Rad) * cos(lat2Rad)))
    }
}