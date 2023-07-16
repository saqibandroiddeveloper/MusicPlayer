package com.example.musicplayer.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.PlayerActivity
import com.example.musicplayer.PlayerActivity.Companion.isPlaying
import com.example.musicplayer.PlayerActivity.Companion.musicListPA
import com.example.musicplayer.PlayerActivity.Companion.musicService
import com.example.musicplayer.PlayerActivity.Companion.playerBinding
import com.example.musicplayer.PlayerActivity.Companion.songPosition
import com.example.musicplayer.R
import com.example.musicplayer.databinding.PlayingNowBinding
import com.example.musicplayer.model.setSongPosition

class NowPlaying : Fragment() {
    companion object{
        @SuppressLint("StaticFieldLeak")
         lateinit var binding : PlayingNowBinding
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.playing_now, container, false)
        binding = PlayingNowBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.tvNP.isSelected = true
        binding.playPauseSongNP.setOnClickListener {
            if (isPlaying) pauseMusic() else playMusic()
        }
        binding.nextSongNP.setOnClickListener {
            setSongPosition(true)
            musicService!!.serviceMediaPlayer()
            Glide.with(this)
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(binding.imageNP)
            binding.tvNP.text = musicListPA[songPosition].title
            musicService!!.showNotification(R.drawable.pause_icon)
            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(musicService != null){
            binding.root.visibility = View.VISIBLE
            Glide.with(this)
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(binding.imageNP)
            binding.tvNP.text = musicListPA[songPosition].title
            if (isPlaying) binding.playPauseSongNP.setIconResource(R.drawable.pause_icon)
            else binding.playPauseSongNP.setIconResource(R.drawable.play_icon)
        }
    }

    private fun playMusic(){
        musicService!!.mediaPlayer!!.start()
        binding.playPauseSongNP.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        playerBinding.playPauseSong.setIconResource(R.drawable.pause_icon)
        isPlaying = true
    }
    private fun pauseMusic(){
        musicService!!.mediaPlayer!!.pause()
        binding.playPauseSongNP.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        playerBinding.playPauseSong.setIconResource(R.drawable.play_icon)
        isPlaying = false
    }

}