package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) { super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT); }
        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0: return new FragmentShow();
                case 1: return new FragmentAdd();
                case 2: return new FragmentDel();
                case 3: return new FragmentUpdate();
                default: return null;
            }
        }
        @Override
        public int getCount() { return 4; }
        @Override
        public CharSequence getPageTitle(int p) {
            return new String[]{"Show", "Add", "Del", "Update"}[p];
        }
    }
}