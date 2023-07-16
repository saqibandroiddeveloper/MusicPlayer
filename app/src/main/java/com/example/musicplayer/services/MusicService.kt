package com.example.musicplayer.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.BaseApp.Companion.CHANNEL_ID
import com.example.musicplayer.BaseApp.Companion.EXIT
import com.example.musicplayer.BaseApp.Companion.NEXT
import com.example.musicplayer.BaseApp.Companion.PLAY
import com.example.musicplayer.BaseApp.Companion.PREVIOUS
import com.example.musicplayer.MainActivity
import com.example.musicplayer.PlayerActivity
import com.example.musicplayer.PlayerActivity.Companion.isNowPlayingId
import com.example.musicplayer.PlayerActivity.Companion.isPlaying
import com.example.musicplayer.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.PlayerActivity.Companion.playerBinding
import com.example.musicplayer.PlayerActivity.Companion.songPosition
import com.example.musicplayer.R
import com.example.musicplayer.bdreciever.NotificationReceiver
import com.example.musicplayer.fragment.NowPlaying
import com.example.musicplayer.model.formatDuration
import com.example.musicplayer.model.getImageArt
import com.example.musicplayer.model.initSeekBar


class MusicService: Service() ,AudioManager.OnAudioFocusChangeListener{
   private var myBinder = MyBinder()
     var mediaPlayer:MediaPlayer? = null
    private lateinit var mediaSession:MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    override fun onBind(intent: Intent?): IBinder {
         this.mediaSession = MediaSessionCompat(baseContext,"Music Player")
            return myBinder
    }
    inner class MyBinder:Binder(){
       fun currentService():MusicService{
           return this@MusicService
       }
    }

    fun showNotification(playPause:Int){
        val intent = Intent(baseContext, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(baseContext,CHANNEL_ID)
            .setContentTitle(musicListPA[songPosition].title)
            .setContentText(musicListPA[songPosition].artist)
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.small_icon_music)
            .setLargeIcon(setImage())
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.back_arrow,"Previous",pendingIntent(PREVIOUS))
            .addAction(playPause,"Play",pendingIntent(PLAY))
            .addAction(R.drawable.farwards,"Next",pendingIntent(NEXT))
            .addAction(R.drawable.ic_baseline_exit_to_app_24,"Exit",pendingIntent(EXIT))
            .build()
        startForeground(13,notification)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun pendingIntent(action: String): PendingIntent {
        val intent = Intent(baseContext, NotificationReceiver::class.java).setAction(action)
        return PendingIntent.getBroadcast(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private fun setImage(): Bitmap? {
        val imageArt = getImageArt(musicListPA[songPosition].path)
        return if (imageArt!=null){
          BitmapFactory.decodeByteArray(imageArt,0,imageArt.size)
        }
        else{
            BitmapFactory.decodeResource(resources,R.drawable.music_icon)
        }
    }

    fun serviceMediaPlayer(){
        PlayerActivity.musicService!!.mediaPlayer?.let {
            it.reset()
            it.setDataSource(musicListPA[songPosition].path)
            it.prepare()
        }
        playerBinding.playPauseSong.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        if (songPosition >= 0 && songPosition < musicListPA.size) {
            Glide.with(this)
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(playerBinding.songImage)
            playerBinding.songName.text = musicListPA[songPosition].title
        }
        initSeekBar()
        isNowPlayingId = musicListPA[songPosition].id
    }
    fun seekBarSetup(){
      runnable = Runnable {
          playerBinding.apply {
              startTime.text = formatDuration(PlayerActivity.musicService!!.mediaPlayer!!.currentPosition.toLong())
              progressBar.progress = mediaPlayer!!.currentPosition
              Handler(Looper.getMainLooper()).postDelayed(runnable,200)
          }
      }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0){
            playerBinding.playPauseSong.setIconResource(R.drawable.play_icon)
            NowPlaying.binding.playPauseSongNP.setIconResource(R.drawable.play_icon)
            isPlaying = false
            showNotification(R.drawable.play_icon)
            mediaPlayer!!.pause()
        }else{
            playerBinding.playPauseSong.setIconResource(R.drawable.pause_icon)
            NowPlaying.binding.playPauseSongNP.setIconResource(R.drawable.pause_icon)
            isPlaying = true
            showNotification(R.drawable.pause_icon)
            mediaPlayer!!.start()
        }

    }


}