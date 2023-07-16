package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.adapter.FavouriteAdaptor
import com.example.musicplayer.databinding.ActivityFavouriteBinding
import com.example.musicplayer.model.Music
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class FavouriteActivity : BaseActivity() {
    @Inject
    lateinit var favouriteBinding: ActivityFavouriteBinding
    private lateinit var favouriteAdaptor: FavouriteAdaptor

    companion object{
        var favouriteSongs:ArrayList<Music>  = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(favouriteBinding.root)
        supportActionBar?.hide()
        /*favouriteSongs = checkSong(favouriteSongs)*/
        favouriteBinding.btnFBack.setOnClickListener { onBackPress() }
        favouriteBinding.apply {
            favouriteRV.setHasFixedSize(true)
            favouriteRV.setItemViewCacheSize(13)
            favouriteRV.layoutManager = GridLayoutManager(this@FavouriteActivity,3)
            favouriteAdaptor = FavouriteAdaptor(this@FavouriteActivity, favouriteSongs)
            favouriteRV.adapter = favouriteAdaptor
            if (favouriteSongs.isEmpty())favouriteBinding.btnShaffle.visibility = View.GONE
            btnShaffle.setOnClickListener {
                val intent = Intent(this@FavouriteActivity, PlayerActivity::class.java)
                intent.putExtra("index",0)
                intent.putExtra("class","FavouriteShuffle")
                startActivity(intent)
            }
        }
    }

    override fun onBackPress() {
        finish()
    }
}