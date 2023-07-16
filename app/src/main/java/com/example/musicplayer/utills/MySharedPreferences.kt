package com.example.musicplayer.utills

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MySharedPreferences @Inject constructor(
    @ApplicationContext private var context: Context
) {
    private var pref: SharedPreferences = context.getSharedPreferences("Main",Context.MODE_PRIVATE)
    private var prefEdit: SharedPreferences.Editor =
        context.getSharedPreferences("Main",Context.MODE_PRIVATE).edit()

    var appColor : String
        get() = pref.getString("appColor","").toString()
        set(value) = prefEdit.putString("appColor",value).apply()
    var colorPosition : Int
        get() = pref.getInt("colorPosition",0)
        set(value) = prefEdit.putInt("colorPosition",value).apply()

}