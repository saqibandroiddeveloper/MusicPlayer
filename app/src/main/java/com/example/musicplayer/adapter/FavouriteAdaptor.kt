package com.example.musicplayer.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicplayer.PlayerActivity
import com.example.musicplayer.databinding.FavouriteListBinding
import com.example.musicplayer.model.Music

class FavouriteAdaptor (private val context: Context,private var musicList:ArrayList<Music>):Adapter<FavouriteAdaptor.MyViewHolder>() {


    inner class MyViewHolder(val binding:FavouriteListBinding):ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(FavouriteListBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.favouriteText.text = musicList[position].title
  /*      Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_icon))
            .into(holder.binding.favouriteImage)
*/
        holder.binding.root.setOnClickListener {
            sendIntent("FavouriteAdaptor",position)
        }
        }
    override fun getItemCount(): Int {
        return musicList.size
    }

    private fun sendIntent(ref:String, pos:Int){
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
}


