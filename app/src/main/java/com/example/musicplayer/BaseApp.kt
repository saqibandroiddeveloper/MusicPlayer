package com.example.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.musicplayer.utills.ConstantObjects.selectedThemePosition
import com.example.musicplayer.utills.MySharedPreferences
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApp:Application() {
    @Inject
    lateinit var mySharedPreferences: MySharedPreferences
    companion object{
       const val CHANNEL_ID = "channel1"
       const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"

    }
    override fun onCreate() {
        super.onCreate()
        selectedThemePosition = mySharedPreferences.colorPosition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID,"Now Playing",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is important to show Songs"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}