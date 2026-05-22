package com.example.a5lab;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerTabStrip;
import com.example.a5lab.fragments.FragmentShow;
import com.example.a5lab.fragments.FragmentAdd;
import com.example.a5lab.fragments.FragmentDel;
import com.example.a5lab.fragments.FragmentUpdate;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        PagerTabStrip pagerTabStrip = findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColorResource(android.R.color.white);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new FragmentShow();
                case 1: return new FragmentAdd();
                case 2: return new FragmentDel();
                case 3: return new FragmentUpdate();
                default: return new FragmentShow();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Смотреть";
                case 1: return "Добавить";
                case 2: return "Удалить";
                case 3: return "Изменить";
                default: return "";
            }
        }
    }
}