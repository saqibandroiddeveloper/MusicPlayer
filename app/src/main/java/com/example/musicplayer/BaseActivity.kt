package com.example.musicplayer

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.utills.Constants.Companion.getDefaultThemeColor
import com.example.musicplayer.utills.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPref : MySharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(getDefaultThemeColor(this).themeResId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        })
    }

    abstract fun onBackPress()
}