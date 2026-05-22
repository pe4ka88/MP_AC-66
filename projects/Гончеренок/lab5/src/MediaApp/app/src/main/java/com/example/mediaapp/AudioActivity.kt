package com.example.mediaapp

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AudioActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        title = "Audio — Гончерёнок К.А. АС-66"

        val btnPlay = findViewById<Button>(R.id.btnPlayAudio)
        val btnPause = findViewById<Button>(R.id.btnPauseAudio)
        val btnStop = findViewById<Button>(R.id.btnStopAudio)
        val btnBack = findViewById<Button>(R.id.btnBackAudio)

        mediaPlayer = MediaPlayer.create(this, R.raw.my_audio)

        btnPlay.setOnClickListener {
            mediaPlayer?.start()
        }

        btnPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }

        btnStop.setOnClickListener {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.my_audio)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}