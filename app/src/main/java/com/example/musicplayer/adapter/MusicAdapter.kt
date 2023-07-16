package com.example.musicplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.MainActivity
import com.example.musicplayer.PlayerActivity
import com.example.musicplayer.PlaylistActivity.Companion.musicPlaylist
import com.example.musicplayer.PlaylistDetails.Companion.currentPlaylistPos
import com.example.musicplayer.R
import com.example.musicplayer.databinding.MusicListItemBinding
import com.example.musicplayer.model.Music
import com.example.musicplayer.model.formatDuration
import com.example.musicplayer.utills.ConstantObjects.getColorFromAttr

class MusicAdapter(private val context: Context,private var musicList:ArrayList<Music>,private val playListDetail:Boolean=false,private val selectionActivity: Boolean=false):Adapter<MusicAdapter.MyViewHolder>() {


   inner class MyViewHolder(private val binding:MusicListItemBinding):ViewHolder(binding.root) {
         val songName = binding.tvMusicName
        val albumName = binding.tvAlbumName
        val songDuration  = binding.tvSongDuration
        val songImage = binding.musicImage
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(MusicListItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.songName.text = musicList[position].title
        holder.albumName.text = musicList[position].album
        holder.songDuration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(holder.songImage)
        when{
            playListDetail->{
                holder.root.setOnClickListener {
                    sendIntent("PlaylistDetails",position)
                }
            }
            selectionActivity->{
               holder.root.setOnClickListener {
                   if (addSong(musicList[position])){
                   holder.root.setBackgroundColor(getColorFromAttr(context,R.attr.AppColor))
                   }else{
                       holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
                   }
               }
            }
            else->{
                holder.root.setOnClickListener {
                    when{
                        MainActivity.search ->sendIntent("MusicAdaptorSearch",position)
                        musicList[position].id == PlayerActivity.isNowPlayingId ->sendIntent("NowPlaying", pos = PlayerActivity.songPosition)
                        else->sendIntent("MusicAdapter",position)
                    }

                }
            }
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<Music>){
       musicList = java.util.ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun addSong(song: Music): Boolean {
        musicPlaylist.ref[currentPlaylistPos].playlist.forEachIndexed{ index, music ->
           if (song.id==music.id){
                musicPlaylist.ref[currentPlaylistPos].playlist.removeAt(index)
                return false
           }
        }
        musicPlaylist.ref[currentPlaylistPos].playlist.add(song)
        return true
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(){
        musicList = ArrayList()
        musicList = musicPlaylist.ref[currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
    private fun sendIntent(ref:String, pos:Int){
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
}


