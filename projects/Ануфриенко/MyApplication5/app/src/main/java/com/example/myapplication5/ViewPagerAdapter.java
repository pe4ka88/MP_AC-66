package com.example.myapplication5;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication5.FragmentAdd;
import com.example.myapplication5.FragmentDel;
import com.example.myapplication5.FragmentShow;
import com.example.myapplication5.FragmentUpdate;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    // Заголовки вкладок — отображаются в PagerTabStrip
    private final String[] PAGE_TITLES = {"Show", "Add", "Del", "Update"};

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
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
        return PAGE_TITLES.length;
    }

    // PagerTabStrip подтягивает заголовки именно отсюда
    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }
}