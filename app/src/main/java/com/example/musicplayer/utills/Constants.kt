package com.example.musicplayer.utills

import android.app.Activity
import androidx.appcompat.view.ContextThemeWrapper
import com.example.musicplayer.R
import com.example.musicplayer.utills.ConstantObjects.selectedThemePosition

class Constants {
    companion object{
        fun getDefaultThemeColor(context: Activity): ContextThemeWrapper {
            when (selectedThemePosition) {
                0 -> {
                    return ContextThemeWrapper(context, R.style.Scheme1)
                }
                1 -> {
                    return ContextThemeWrapper(context, R.style.Scheme2)
                }
                2 -> {
                    return ContextThemeWrapper(context, R.style.Scheme3)
                }
                3 -> {
                    return ContextThemeWrapper(context, R.style.Scheme4)
                }
                4 -> {
                    return ContextThemeWrapper(context, R.style.Scheme5)
                }
                else -> {
                    return ContextThemeWrapper(context, R.style.Scheme6)
                }
            }
        }
    }
}