package com.example.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.FavouriteActivity.Companion.favouriteSongs
import com.example.musicplayer.PlayerActivity.Companion.isPlaying
import com.example.musicplayer.PlayerActivity.Companion.musicService
import com.example.musicplayer.PlaylistActivity.Companion.musicPlaylist
import com.example.musicplayer.adapter.MusicAdapter
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.model.Music
import com.example.musicplayer.model.MusicPlaylist
import com.example.musicplayer.model.exitFromApp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var mainBinding: ActivityMainBinding
    private lateinit var navToggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var musicRecyclerView: RecyclerView

    companion object {
        lateinit var musicListMA: ArrayList<Music>
        lateinit var musiclistSearch :ArrayList<Music>
        var search = false
        var sortOrder = 0
        val sortList = arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC",MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.SIZE + " DESC")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)
        checkPermission()
        //for retrieve shared data
        favouriteSongs = ArrayList()
        val editor = getSharedPreferences("FAVOURITE", MODE_PRIVATE)
        val jsonArray = editor.getString("favouriteSongs",null)
        val tokenType = object : TypeToken<ArrayList<Music>>(){}.type
        if (jsonArray!=null){
            val data:ArrayList<Music> = GsonBuilder().create().fromJson(jsonArray,tokenType)
            favouriteSongs.addAll(data)
        }

        musicPlaylist = MusicPlaylist()
        val jsonPlaylistArray = editor.getString("playlistSongs",null)
        if (jsonPlaylistArray!=null){
            val dataPlaylist:MusicPlaylist = GsonBuilder().create().fromJson(jsonPlaylistArray,MusicPlaylist::class.java)
            musicPlaylist = dataPlaylist
        }

        navToggle = ActionBarDrawerToggle(this, mainBinding.root, R.string.open, R.string.close)
        mainBinding.root.addDrawerListener(navToggle)
        navToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainBinding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.feedback -> startActivity(Intent(this,FeedbackActivity::class.java))
                R.id.about -> startActivity(Intent(this,AboutActivity::class.java))
                R.id.setting -> {
                    themeResultCallback.launch(Intent(this,SettingActivity::class.java))
                }
                R.id.exit -> exitDialog()
            }
            true
        }
        search = false
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder = sortEditor.getInt("sortOrder",0)
        musicListMA = getAllAudio()
        musicAdapter = MusicAdapter(this, musicListMA)
        musicRecyclerView = mainBinding.musicRv
        musicRecyclerView.setHasFixedSize(true)
        musicRecyclerView.layoutManager = LinearLayoutManager(this)
        musicRecyclerView.adapter = musicAdapter
        mainBinding.apply {
            btnShuffle.setOnClickListener {
                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                intent.putExtra("index",0)
                intent.putExtra("class","MainActivity")
                startActivity(intent)
            }
            btnFavourite.setOnClickListener {
                val intent  = Intent(this@MainActivity,FavouriteActivity::class.java)
                startActivity(intent)
            }
            btnPlaylist.setOnClickListener {
                val intent  = Intent(this@MainActivity,PlaylistActivity::class.java)
                startActivity(intent)
            }
        }
        mainBinding.textView4.text = buildString {
            append("Total Songs ")
            append(": ")
            append(musicAdapter.itemCount)
        }
    }

    override fun onBackPress() {
    exitDialog()
    }
    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        when (it) {
            true -> { getAllAudio() }
            false -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val themeResultCallback = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
         val data=it.data
        val isChanged=data?.getBooleanExtra("theme",false)
            if (isChanged == true){
                recreate()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navToggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Range")
    private fun getAllAudio(): ArrayList<Music> {
        Log.d("cvv", "getAllAudio: ${sortList[sortOrder]}")
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortList[sortOrder],
            null
        )
        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val idC = it.getString(it.getColumnIndex(MediaStore.Audio.Media._ID))
                    val titleC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val albumC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC =
                        it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUri = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(
                        id = idC, title = titleC, album = albumC, artist = artistC,
                        path = pathC, duration = durationC, artUri = artUri
                    )
                    val file = File(music.path)
                    if (file.exists()) {
                        tempList.add(music)
                    }
                } while (it.moveToNext())
            }
            it.close()
        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isPlaying && musicService!=null){
            exitFromApp()
        }

    }

    override fun onResume() {
        super.onResume()
        // store favourite songs in shared preferences
        val editor = getSharedPreferences("FAVOURITE", MODE_PRIVATE).edit()
        val jsonArray = GsonBuilder().create().toJson(favouriteSongs)
        editor.putString("favouriteSongs",jsonArray)
        val jsonPlaylistArray = GsonBuilder().create().toJson(musicPlaylist)
        editor.putString("playlistSongs",jsonPlaylistArray)
        editor.apply()

        //for sorting
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue = sortEditor.getInt("sortOrder",0)
        if (sortOrder != sortValue){
            sortOrder = sortValue
            musicListMA = getAllAudio()
            musicAdapter.updateMusicList(musicListMA)
        }
    }
    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            println("Permission isGranted")
        }
    }
    private fun exitDialog(){
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.title))
            .setMessage(resources.getString(R.string.supporting_text))
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                exitFromApp()
                finish()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
       val searchView = menu?.findItem(R.id.btnSearch)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean =true

            override fun onQueryTextChange(newText: String?): Boolean {
                musiclistSearch = ArrayList()
                if (newText != null){
                    val userInput = newText.lowercase()
                    musicListMA.forEach { song->
                        if (song.title.lowercase().contains(userInput)) musiclistSearch.add(song)

                        search = true
                        musicAdapter.updateMusicList(musiclistSearch)

                    }
                }

                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }



}