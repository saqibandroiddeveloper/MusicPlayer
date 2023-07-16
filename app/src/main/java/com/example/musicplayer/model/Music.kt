package com.example.musicplayer.model

import android.media.MediaMetadataRetriever
import com.example.musicplayer.FavouriteActivity
import com.example.musicplayer.PlayerActivity
import com.example.musicplayer.PlayerActivity.Companion.musicService
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(
    val id:String,
    val title:String,
    val album:String,
    val artist:String,
    val path:String,
    val duration: Long = 0,
   val artUri:String
)
class Playlist{
    lateinit var name:String
    lateinit var playlist: ArrayList<Music>
    lateinit var createdBy:String
    lateinit var createdOn:String
}
class MusicPlaylist{
    var ref:ArrayList<Playlist> = ArrayList()
}
fun formatDuration(duration: Long):String{
    val minute = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-minute*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%02d:%02d",minute,seconds)
}
fun getImageArt(path: String):ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
fun setSongPosition(increment: Boolean){
  if (!PlayerActivity.repeat){
      if (increment){
          if (PlayerActivity.songPosition == PlayerActivity.musicListPA.size - 1)
              PlayerActivity.songPosition =0
          else
              PlayerActivity.songPosition++
      }else{
          if (PlayerActivity.songPosition ==0)
              PlayerActivity.songPosition = PlayerActivity.musicListPA.size-1
          else
              PlayerActivity.songPosition--
      }
  }
}
fun initSeekBar(){
    PlayerActivity.playerBinding.apply {
        startTime.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
        endTime.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
        progressBar.progress = 0
        progressBar.max = musicService!!.mediaPlayer!!.duration
    }
}
fun exitFromApp(){
    if(!PlayerActivity.isPlaying && musicService !=null){
        musicService!!.audioManager.abandonAudioFocus(musicService)
       musicService!!.stopForeground(true)
        musicService!!.mediaPlayer!!.release()
        musicService =null
        exitProcess(1)
    }
}

fun favouriteChecker(id: String):Int{
    PlayerActivity.isFavourite = false
    FavouriteActivity.favouriteSongs.forEachIndexed { index, music ->
        if (id==music.id){
            PlayerActivity.isFavourite = true
            return index
        }
    }
    return -1
}
fun checkSong(playlist: ArrayList<Music>):ArrayList<Music>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if (file.exists())
            playlist.removeAt(index)
    }
    return playlist
}