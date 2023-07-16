package com.example.musicplayer

import android.os.Bundle
import com.example.musicplayer.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AboutActivity : BaseActivity() {
    @Inject
    lateinit var aboutBinding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(aboutBinding.root)
        supportActionBar?.title = "About"
    }

    override fun onBackPress() {
        finish()
    }
}