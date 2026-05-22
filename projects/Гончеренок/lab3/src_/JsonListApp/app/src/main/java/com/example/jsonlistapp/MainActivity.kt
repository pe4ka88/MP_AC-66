package com.example.jsonlistapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.jsonlistapp.ui.DetailFragment
import com.example.jsonlistapp.ui.ListFragment

class MainActivity : AppCompatActivity(), ListFragment.OnPostClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ListFragment())
                .commit()
        }
    }

    override fun onPostClick(postId: Int, userId: Int, title: String, body: String) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                DetailFragment.newInstance(postId, userId, title, body)
            )
            .addToBackStack(null)
            .commit()
    }
}