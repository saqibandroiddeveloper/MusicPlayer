package com.example.musicplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicplayer.PlaylistActivity.Companion.musicPlaylist
import com.example.musicplayer.PlaylistDetails
import com.example.musicplayer.databinding.PlaylistListBinding
import com.example.musicplayer.model.Playlist
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdaptor(private val context: Context, private var playlistList: ArrayList<Playlist>) :
    Adapter<PlaylistAdaptor.MyViewHolder>() {


    inner class MyViewHolder(val binding: PlaylistListBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            PlaylistListBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.playListName.text = playlistList[position].name
        holder.binding.playListName.isSelected = true

        holder.binding.btnPLDelete.setOnClickListener {
            deleteDialog(position)
        }
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }

    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refreshList() {
        playlistList = ArrayList()
        playlistList.addAll(musicPlaylist.ref)
        notifyDataSetChanged()
    }

    private fun deleteDialog(position: Int) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete")
            .setMessage("Are you want Delete?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Yes") { dialog, _ ->
                musicPlaylist.ref.removeAt(position)
                refreshList()
                dialog.dismiss()
            }
            .show()
    }
}


