package com.example.labb5new5;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);

        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            String[] titles = {"Show", "Add", "Del", "Update"};

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return new FragmentShow();
                    case 1: return new FragmentAdd();
                    case 2: return new FragmentDel();
                    case 3: return new FragmentUpdate();
                }
                return null;
            }

            @Override
            public int getCount() { return titles.length; }

            @Override
            public CharSequence getPageTitle(int position) { return titles[position]; }
        });
    }
}
