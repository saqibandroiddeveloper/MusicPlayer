package com.example.musicplayer

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.MainActivity.Companion.sortOrder
import com.example.musicplayer.adapter.ColorAdaptor
import com.example.musicplayer.databinding.ActivitySettingBinding
import com.example.musicplayer.utills.ConstantObjects.selectedThemePosition
import com.example.musicplayer.utills.Constants.Companion.getDefaultThemeColor
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private var isthemechanged=false
@AndroidEntryPoint
class SettingActivity : BaseActivity(),ColorAdaptor.ThemeInterface {
    @Inject
    lateinit var settingBinding: ActivitySettingBinding
    private lateinit var colorAdaptor :ColorAdaptor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(settingBinding.root)
        supportActionBar?.title = "Setting"

        settingBinding.apply {
            sortBy.setOnClickListener {
                val menuList = arrayOf("Recently Add","Song Title","File Size")
                val builder = MaterialAlertDialogBuilder(this@SettingActivity)
                builder.setTitle("Sorting")
                    .setPositiveButton("Yes"){_,_ ->
                        val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                            editor.putInt("sortOrder",sortOrder)
                            editor.apply()
                    }
                    .setSingleChoiceItems(menuList, sortOrder){ _, item ->
                        sortOrder = item
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }
        }
        colorAdaptor = ColorAdaptor(sharedPref,this,this@SettingActivity)
        val colorArray: MutableList<String> = mutableListOf(
            "#D003FF","#e52222","#480eea","#0ecdea","#16a30e","#000000"
        )

        settingBinding.colorRV.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        settingBinding.colorRV.adapter = colorAdaptor
         colorAdaptor.submitList(colorArray)
    }



    override fun onBackPress() {
        val intent=Intent()
        intent.putExtra("theme", isthemechanged)
        setResult(RESULT_OK,intent)
        finish()
    }

    override fun getThemePosition(pos: Int) {
        sharedPref.colorPosition = pos
        selectedThemePosition = pos
        isthemechanged=true
        setTheme(getDefaultThemeColor(this@SettingActivity).themeResId)
        this.recreate()
        isthemechanged=true
    }
}