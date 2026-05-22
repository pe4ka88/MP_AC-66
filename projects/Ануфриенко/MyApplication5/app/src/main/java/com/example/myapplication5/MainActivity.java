package com.example.myapplication5;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication5.R;
import com.example.myapplication5.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager       viewPager;
    private ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager    = findViewById(R.id.viewPager);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Чтобы все фрагменты держались в памяти и onResume
        // вызывался корректно при переключении вкладок
        viewPager.setOffscreenPageLimit(3);
    }
}