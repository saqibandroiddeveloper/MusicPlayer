package com.example.musicplayer.bdreciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.BaseApp.Companion.EXIT
import com.example.musicplayer.BaseApp.Companion.NEXT
import com.example.musicplayer.BaseApp.Companion.PLAY
import com.example.musicplayer.BaseApp.Companion.PREVIOUS
import com.example.musicplayer.PlayerActivity.Companion.fIndex
import com.example.musicplayer.PlayerActivity.Companion.isPlaying
import com.example.musicplayer.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.PlayerActivity.Companion.musicService
import com.example.musicplayer.PlayerActivity.Companion.playerBinding
import com.example.musicplayer.PlayerActivity.Companion.songPosition
import com.example.musicplayer.R
import com.example.musicplayer.fragment.NowPlaying.Companion.binding
import com.example.musicplayer.model.exitFromApp
import com.example.musicplayer.model.favouriteChecker
import com.example.musicplayer.model.setSongPosition

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
         when(intent?.action){
             PREVIOUS-> preOrNext(increment = false,context!!)
             NEXT-> preOrNext(increment = true,context!!)
             PLAY-> if (isPlaying)pauseMusic() else playMusic()
             EXIT-> {
                 exitFromApp()
             }
         }
    }

    private fun playMusic(){
        playerBinding.playPauseSong.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        musicService!!.showNotification(R.drawable.pause_icon)
        binding.playPauseSongNP.setIconResource(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        playerBinding.playPauseSong.setIconResource(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        musicService!!.showNotification(R.drawable.play_icon)
        binding.playPauseSongNP.setIconResource(R.drawable.play_icon)
    }
    private fun preOrNext(increment: Boolean,context: Context){
            setSongPosition(increment)
            musicService!!.serviceMediaPlayer()
        Glide.with(context)
            .load(musicListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(binding.imageNP)
        binding.tvNP.text = musicListPA[songPosition].title
           playMusic()
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        if (isPlaying) playerBinding.btnFavouritePA.setImageResource(R.drawable.ic_baseline_favorite_24)
        else playerBinding.btnFavouritePA.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

}