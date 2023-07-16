package com.example.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.adapter.PlaylistAdaptor
import com.example.musicplayer.databinding.ActivityPlaylistBinding
import com.example.musicplayer.databinding.AddPlaylistLayoutBinding
import com.example.musicplayer.model.MusicPlaylist
import com.example.musicplayer.model.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
@AndroidEntryPoint
class PlaylistActivity : BaseActivity() {
    @Inject
    lateinit var playlistBinding: ActivityPlaylistBinding
    lateinit var playlistAdaptor: PlaylistAdaptor
    companion object{
        var musicPlaylist = MusicPlaylist()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(playlistBinding.root)
        supportActionBar?.hide()
        playlistBinding.apply {
            playlistAdaptor = PlaylistAdaptor(this@PlaylistActivity, musicPlaylist.ref)
            playlistRV.setHasFixedSize(true)
            playlistRV.setItemViewCacheSize(13)
            playlistRV.layoutManager = GridLayoutManager(this@PlaylistActivity,2)
            playlistRV.adapter = playlistAdaptor

            btnAddPlaylist.setOnClickListener {
         customAlertDialog()
            }

            btnPBack.setOnClickListener {
                onBackPress()
            }
        }
    }
   private fun customAlertDialog(){
       val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_layout,playlistBinding.root,false)
       val binder = AddPlaylistLayoutBinding.bind(customDialog)
       val builder = MaterialAlertDialogBuilder(this)
       builder.setView(customDialog)
           .setTitle("Playlist Add")
           .setNegativeButton("cancel"){dialog,_ ->
               dialog.dismiss()
           }
           .setPositiveButton("Add"){ dialog,_ ->
               val playlistName = binder.playlistName.text
               val createdBy = binder.yourName.text
               if (playlistName != null && createdBy != null){
                   if (playlistName.isNotEmpty() && createdBy.isNotEmpty()){
                       addPlaylist(playlistName.toString(),createdBy.toString())
                   }
               }
               dialog.dismiss()
           }
           .show()
   }

    private fun addPlaylist(playlistName: String, createdBy: String) {
     var playlistExist = false
        for (i in musicPlaylist.ref){
            if (playlistName == i.name){
                playlistExist = true
            }
        }
        if (playlistExist) Toast.makeText(this, "Playlist Already Exist", Toast.LENGTH_SHORT).show()
        else{
          val tempPlaylist = Playlist()
            tempPlaylist.name = playlistName
            tempPlaylist.createdBy = createdBy
            tempPlaylist.playlist = ArrayList()
            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn =sdf.format(calendar)
            musicPlaylist.ref.add(tempPlaylist)
            playlistAdaptor.refreshList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        playlistAdaptor.notifyDataSetChanged()
    }
    override fun onBackPress() {
     finish()
    }
}
