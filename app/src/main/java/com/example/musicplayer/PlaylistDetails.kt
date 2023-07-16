package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.PlaylistActivity.Companion.musicPlaylist
import com.example.musicplayer.adapter.MusicAdapter
import com.example.musicplayer.databinding.PlaylistDetailsBinding
import com.example.musicplayer.model.checkSong
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class PlaylistDetails : BaseActivity() {
    @Inject
    lateinit var playlistDetailsBinding: PlaylistDetailsBinding
    lateinit var pLAdaptor:MusicAdapter
    companion object{
         var currentPlaylistPos = -1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(playlistDetailsBinding.root)
        currentPlaylistPos = intent.getIntExtra("index",-1)
        musicPlaylist.ref[currentPlaylistPos].playlist = checkSong(musicPlaylist.ref[currentPlaylistPos].playlist)
        playlistDetailsBinding.apply {
            playListDetailRV.layoutManager = LinearLayoutManager(this@PlaylistDetails)
            pLAdaptor = MusicAdapter(this@PlaylistDetails, musicPlaylist.ref[currentPlaylistPos].playlist,true)
            playListDetailRV.adapter = pLAdaptor
            btnPLShuffle.setOnClickListener {
                val intent = Intent(this@PlaylistDetails, PlayerActivity::class.java)
                intent.putExtra("index",0)
                intent.putExtra("class","PlaylistDetailShuffle")
                startActivity(intent)
            }
            addPLBtn.setOnClickListener {
                startActivity(Intent(this@PlaylistDetails,SelectionActivity::class.java))
            }
            btnRemovePLBtn.setOnClickListener {
                removeSongDialog()
            }
            btnPDBack.setOnClickListener {
                onBackPress()
            }
        }
    }
    private fun removeSongDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.remove_title))
            .setMessage(resources.getString(R.string.remove_Song))
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
             musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                pLAdaptor.refreshList()
            }
            .show()
    }
    override fun onBackPress() {
        finish()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        playlistDetailsBinding.apply {
            tvPlaylistName.text = musicPlaylist.ref[currentPlaylistPos].name
            tvMoreInfo.text = " Total ${pLAdaptor.itemCount} Songs \n\n"+
                    " Created On:\n${musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n"+
                    " -- ${musicPlaylist.ref[currentPlaylistPos].createdBy}"
            if (pLAdaptor.itemCount > 0){
                Glide.with(this@PlaylistDetails)
                    .load(musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                    .apply(RequestOptions().placeholder(R.drawable.music_icon))
                    .into(playlistDetailImg)
                btnPLShuffle.visibility = View.VISIBLE
            }
        }
        pLAdaptor.notifyDataSetChanged()

        val editor = getSharedPreferences("FAVOURITE", MODE_PRIVATE).edit()
        val jsonPlaylistArray = GsonBuilder().create().toJson(musicPlaylist)
        editor.putString("playlistSongs",jsonPlaylistArray)
        editor.apply()
    }
}