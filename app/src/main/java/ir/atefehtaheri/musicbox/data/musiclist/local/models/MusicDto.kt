package ir.atefehtaheri.musicbox.data.musiclist.local.models

data class MusicDto(
    val id:Long,
    val title : String,
    val artist : String,
    val duration:Long,
    val image: String?,
    val filepath:String
)


fun MusicDto.durationToMinutes(): String {
    val totalSeconds = this.duration / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
