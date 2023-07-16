package com.example.musicplayer

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MainActivity.Companion.musicListMA
import com.example.musicplayer.MainActivity.Companion.musiclistSearch
import com.example.musicplayer.adapter.MusicAdapter
import com.example.musicplayer.databinding.ActivitySelectionBinding

class SelectionActivity : BaseActivity() {
    private lateinit var selectionBinding: ActivitySelectionBinding
    lateinit var selectionAdaptor:MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectionBinding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(selectionBinding.root)
        selectionBinding.apply {
            selectionRV.setItemViewCacheSize(10)
            selectionRV.setHasFixedSize(true)
            selectionRV.layoutManager = LinearLayoutManager(this@SelectionActivity)
            selectionAdaptor = MusicAdapter(this@SelectionActivity, musicListMA, selectionActivity = true)
            selectionRV.adapter = selectionAdaptor
            btnSBack.setOnClickListener {
                onBackPress()
            }
        }
        val searchView = selectionBinding.selectionSearch
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean =true
            override fun onQueryTextChange(newText: String?): Boolean {
                musiclistSearch = ArrayList()
                if (newText != null){
                    val userInput = newText.lowercase()
                    musicListMA.forEach { song->
                        if (song.title.lowercase().contains(userInput)) musiclistSearch.add(song)
                        MainActivity.search = true
                        selectionAdaptor.updateMusicList(musiclistSearch)

                    }
                }

                return true
            }

        })

    }

    override fun onBackPress() {
        finish()
    }
}