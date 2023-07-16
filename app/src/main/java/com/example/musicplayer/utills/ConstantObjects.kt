package com.example.musicplayer.utills

import android.content.Context
import android.util.TypedValue

object ConstantObjects {
    var selectedThemePosition = 0
    fun getColorFromAttr(context: Context, attrResId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrResId, typedValue, true)
        return typedValue.data
    }
}