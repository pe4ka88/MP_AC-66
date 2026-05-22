package com.example.mediaapp

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        title = "Video — Гончерёнок К.А. АС-66"

        videoView = findViewById(R.id.videoView)

        val btnStart = findViewById<Button>(R.id.btnStartVideo)
        val btnPause = findViewById<Button>(R.id.btnPauseVideo)
        val btnStop = findViewById<Button>(R.id.btnStopVideo)
        val btnBack = findViewById<Button>(R.id.btnBackVideo)

        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.my_video}")
        videoView.setVideoURI(videoUri)

        btnStart.setOnClickListener {
            videoView.start()
        }

        btnPause.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
            }
        }

        btnStop.setOnClickListener {
            videoView.stopPlayback()
            videoView.setVideoURI(videoUri)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }
}