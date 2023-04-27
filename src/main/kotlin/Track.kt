import dev.inmo.tgbotapi.types.Seconds

data class Track(
    var coordinates: MutableList<Coordinates> = mutableListOf(),
    var timestamp: MutableList<Seconds> = mutableListOf()
)