package com.example.musicplayer.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.musicplayer.databinding.ThemesSampleLayoutBinding
import com.example.musicplayer.utills.MySharedPreferences

class ColorAdaptor(mySharedPreference: MySharedPreferences, private val themeInterface: ThemeInterface,
                   private val context: Activity) :
    androidx.recyclerview.widget.ListAdapter<String, ColorAdaptor.MyViewHolder>(ColorAdaptorDiff()) {
    var selectedPos = mySharedPreference.colorPosition
    inner class MyViewHolder(private val binding: ThemesSampleLayoutBinding) :
        ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos == -1) {
                    return@setOnClickListener
                }
                if (selectedPos == pos) {
                    return@setOnClickListener
                } else {
                    val temPos = selectedPos
                    selectedPos = pos
                    notifyItemChanged(temPos)
                }
                notifyItemChanged(selectedPos)


                themeInterface.getThemePosition(pos)
            }
        }
        fun onColorBind(item: String?,pos: Int) {
            item?.let {
                binding.ivThemes.setBackgroundColor(Color.parseColor(it))
                if (pos == selectedPos) {
                    binding.ivTick.visibility = View.VISIBLE
                } else {
                    binding.ivTick.visibility = View.GONE
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ThemesSampleLayoutBinding.inflate(LayoutInflater.from(context))
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onColorBind(getItem(position),position)
    }

    class ColorAdaptorDiff() : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    interface ThemeInterface {
        fun getThemePosition(pos: Int)
    }
}