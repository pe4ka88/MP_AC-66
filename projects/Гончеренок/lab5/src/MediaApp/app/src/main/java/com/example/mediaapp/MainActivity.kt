package com.example.mediaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "MediaApp — Гончерёнок К.А. АС-66"

        val btnAudio = findViewById<Button>(R.id.btnAudio)
        val btnVideo = findViewById<Button>(R.id.btnVideo)
        val btnPhoto = findViewById<Button>(R.id.btnPhoto)

        btnAudio.setOnClickListener {
            startActivity(Intent(this, AudioActivity::class.java))
        }

        btnVideo.setOnClickListener {
            startActivity(Intent(this, VideoActivity::class.java))
        }

        btnPhoto.setOnClickListener {
            startActivity(Intent(this, PhotoActivity::class.java))
        }
    }
}