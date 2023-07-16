package com.example.musicplayer

import android.os.Bundle
import android.widget.Toast
import com.example.musicplayer.databinding.ActivityFeedbackBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedbackActivity : BaseActivity() {
    @Inject
    lateinit var feedbackBinding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(feedbackBinding.root)
        supportActionBar?.title = "Feedback"
        feedbackBinding.buttonSubmit.setOnClickListener {
            Toast.makeText(this, "Feedback Submitted Successfully", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPress() {
        finish()
    }
}