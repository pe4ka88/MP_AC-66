package com.example.lab3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lab3.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val fragment = PostDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("post_id", intent.getIntExtra("post_id", 0))
                    putInt("post_userId", intent.getIntExtra("post_userId", 0))
                    putString("post_title", intent.getStringExtra("post_title"))
                    putString("post_body", intent.getStringExtra("post_body"))
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(binding.detailContainer.id, fragment)
                .commit()
        }
    }
}