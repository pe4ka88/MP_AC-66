package com.example.myapplication3;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new ListFragment())
                .commit();
    }

    public void openDetail(Photo photo) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, DetailFragment.newInstance(photo))
                .addToBackStack(null)
                .commit();
    }
}