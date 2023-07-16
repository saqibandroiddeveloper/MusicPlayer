package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.FavouriteActivity.Companion.favouriteSongs
import com.example.musicplayer.MainActivity.Companion.musicListMA
import com.example.musicplayer.MainActivity.Companion.musiclistSearch
import com.example.musicplayer.PlaylistActivity.Companion.musicPlaylist
import com.example.musicplayer.PlaylistDetails.Companion.currentPlaylistPos
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.example.musicplayer.model.*
import com.example.musicplayer.services.MusicService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : BaseActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var playerBinding: ActivityPlayerBinding
        lateinit var musicListPA: ArrayList<Music>
        var songPosition = 0
        var isPlaying = false
        var musicService: MusicService? = null
        var repeat = false
        var mint15 = false
        var mint30 = false
        var mint60 = false
        var isNowPlayingId = ""
        var isFavourite = false
        var fIndex = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)
        supportActionBar?.hide()
        initAllLayout()
        playerBinding.apply {
            playPauseSong.setOnClickListener {
                if (isPlaying)
                    pauseMusic()
                else
                    playMusic()
            }
            preSong.setOnClickListener {
                preOrNext(false)
            }
            nextSong.setOnClickListener {
                preOrNext(true)
            }
            progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
            btnRepeat.setOnClickListener {
                if (!repeat) {
                    repeat = true
                    btnRepeat.setColorFilter(
                        ContextCompat.getColor(
                            this@PlayerActivity,
                            R.color.teal_700
                        )
                    )
                } else {
                    repeat = false
                    btnRepeat.setColorFilter(
                        ContextCompat.getColor(
                            this@PlayerActivity,
                            R.color.app_btn
                        )
                    )
                }
            }
            btnEqulizer.setOnClickListener {
                try {
                    val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                    eqIntent.putExtra(
                        AudioEffect.EXTRA_AUDIO_SESSION,
                        musicService!!.mediaPlayer!!.audioSessionId
                    )
                    eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                    eqIntent.putExtra(
                        AudioEffect.EXTRA_CONTENT_TYPE,
                        AudioEffect.CONTENT_TYPE_MUSIC
                    )
                    startActivityForResult(eqIntent, 13)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@PlayerActivity,
                        "Your Mobile is Incompatible",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            btnTimer.setOnClickListener {
                val timer = mint15 || mint30 || mint60
                if (!timer) bottomSheetDialog()
                else {
                    MaterialAlertDialogBuilder(this@PlayerActivity)
                        .setTitle("Stop Time")
                        .setMessage("Are you want to stop Timer?")
                        .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                            mint15 = false
                            mint30 = false
                            mint60 = false
                            playerBinding.btnTimer.setColorFilter(
                                ContextCompat.getColor(
                                    this@PlayerActivity,
                                    R.color.app_btn
                                )
                            )
                        }
                        .show()
                }
            }
            btnShare.setOnClickListener {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "audio/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
                startActivity(Intent.createChooser(shareIntent, "Music Share"))
            }
            btnFavouritePA.setOnClickListener {
                if (isFavourite){
                    isFavourite = false
                    playerBinding.btnFavouritePA.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    favouriteSongs.removeAt(fIndex)
                } else{
                    isFavourite = true
                    playerBinding.btnFavouritePA.setImageResource(R.drawable.ic_baseline_favorite_24)
                    favouriteSongs.add(musicListPA[songPosition])
                }
            }
            btnBackArrow.setOnClickListener {
                onBackPress()
            }
        }

    }

    private fun setLayout() {
        fIndex = favouriteChecker(musicListPA[songPosition].id)
        if (songPosition >= 0 && songPosition < musicListPA.size) {
            Glide.with(this)
                .load(musicListPA[songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon))
                .into(playerBinding.songImage)
            playerBinding.songName.text = musicListPA[songPosition].title
            if (repeat) playerBinding.btnRepeat.setColorFilter(
                ContextCompat.getColor(
                    this@PlayerActivity,
                    R.color.teal_700
                )
            )
            if (mint15 || mint30 || mint60) playerBinding.btnTimer.setColorFilter(
                ContextCompat.getColor(
                    this@PlayerActivity,
                    R.color.teal_700
                )
            )
            if (isFavourite) playerBinding.btnFavouritePA.setImageResource(R.drawable.ic_baseline_favorite_24)
            else playerBinding.btnFavouritePA.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    private fun initMediaPlayer() {
        if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
        musicService!!.mediaPlayer?.let {
            it.reset()
            it.setDataSource(musicListPA[songPosition].path)
            it.prepare()
            it.start()
            isPlaying = true
            playerBinding.playPauseSong.setIconResource(R.drawable.pause_icon)
        }
        musicService!!.showNotification(R.drawable.pause_icon)
        initSeekBar()
        musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        isNowPlayingId = musicListPA[songPosition].id
    }

    private fun initAllLayout() {
        songPosition = intent.getIntExtra("index", -1)
        when (intent.getStringExtra("class")) {
            "MusicAdaptorSearch" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(musiclistSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.clear()
                musicListPA.addAll(musicListMA)
                setLayout()
            }
            "MainActivity" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.clear()
                musicListPA.addAll(musicListMA)
                musicListPA.shuffle()
                setLayout()
            }
            "FavouriteShuffle"->{
                startService()
                musicListPA = ArrayList()
                musicListPA.clear()
                musicListPA.addAll(favouriteSongs)
                musicListPA.shuffle()
                setLayout()
            }
            "NowPlaying"->{
                setLayout()
                playerBinding.apply {
                    startTime.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                    endTime.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                    progressBar.progress = musicService!!.mediaPlayer!!.currentPosition
                    progressBar.max = musicService!!.mediaPlayer!!.duration
                }

               if (isPlaying) playerBinding.playPauseSong.setIconResource(R.drawable.pause_icon)
                else playerBinding.playPauseSong.setIconResource(R.drawable.play_icon)
            }
            "FavouriteAdaptor"->{
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(favouriteSongs)
                setLayout()
            }
            "PlaylistDetails"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(musicPlaylist.ref[currentPlaylistPos].playlist)
                setLayout()
            }
            "PlaylistDetailShuffle"->{
                val intent = Intent(this,MusicService::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(musicPlaylist.ref[currentPlaylistPos].playlist)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun playMusic() {
        playerBinding.playPauseSong.setIconResource(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.showNotification(R.drawable.pause_icon)
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        playerBinding.playPauseSong.setIconResource(R.drawable.play_icon)
        isPlaying = false
        musicService!!.showNotification(R.drawable.play_icon)
        musicService!!.mediaPlayer!!.pause()
    }

    private fun preOrNext(increment: Boolean) {
        setSongPosition(increment)
        initMediaPlayer()
        setLayout()
    }

    override fun onBackPress() {
        finish()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        initMediaPlayer()
        musicService!!.seekBarSetup()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)


    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        initMediaPlayer()
        try {
            initAllLayout()
        } catch (e: Exception) {
            Log.d("TAG", "onCompletion: ${e.message}")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK) return
    }
   private fun startService(){
       val intent = Intent(this, MusicService::class.java)
       bindService(intent, this, BIND_AUTO_CREATE)
       startService(intent)
   }
    private fun bottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.buttom_sheet)
        dialog.show()
        dialog.apply {
            findViewById<ImageView>(R.id.mint_15)?.setOnClickListener {
                Toast.makeText(baseContext, "Music Will Stop After 15 mint", Toast.LENGTH_SHORT)
                    .show()
                playerBinding.btnTimer.setColorFilter(
                    ContextCompat.getColor(
                        this@PlayerActivity,
                        R.color.teal_700
                    )
                )
                mint15 = true
                Thread {
                    Thread.sleep(15 * 60000)
                    if (mint15) exitFromApp()
                }.start()
                dialog.dismiss()
            }
            findViewById<ImageView>(R.id.mint_30)?.setOnClickListener {
                Toast.makeText(baseContext, "Music Will Stop After 30 mint", Toast.LENGTH_SHORT)
                    .show()
                playerBinding.btnTimer.setColorFilter(
                    ContextCompat.getColor(
                        this@PlayerActivity,
                        R.color.teal_700
                    )
                )
                mint30 = true
                Thread {
                    Thread.sleep((30 * 60000).toLong())
                    if (mint30) exitFromApp()
                }.start()
                dialog.dismiss()
            }
            findViewById<ImageView>(R.id.mint_60)?.setOnClickListener {
                Toast.makeText(baseContext, "Music Will Stop After 60 mint", Toast.LENGTH_SHORT)
                    .show()
                playerBinding.btnTimer.setColorFilter(
                    ContextCompat.getColor(
                        this@PlayerActivity,
                        R.color.teal_700
                    )
                )
                mint60 = true
                Thread {
                    Thread.sleep(60 * 60000)
                    if (mint60) exitFromApp()
                }.start()
                dialog.dismiss()
            }
        }
    }

}