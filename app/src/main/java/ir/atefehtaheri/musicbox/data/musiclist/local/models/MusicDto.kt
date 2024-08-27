package ir.atefehtaheri.musicbox.data.musiclist.local.models

data class MusicDto(
    val id:Long,
    val title : String,
    val artist : String,
    val duration:Long,
    val image: String?,
    val filepath:String
)
