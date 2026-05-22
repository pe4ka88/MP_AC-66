package com.example.lab5;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles = new String[]{"Show", "Add", "Del", "Update"};
    private OnDatabaseChangedListener listener;

    public ViewPagerAdapter(@NonNull FragmentManager fm, OnDatabaseChangedListener listener) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.listener = listener;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentShow();
            case 1:
                FragmentAdd fragmentAdd = new FragmentAdd();
                fragmentAdd.setListener(listener);
                return fragmentAdd;
            case 2:
                FragmentDel fragmentDel = new FragmentDel();
                fragmentDel.setListener(listener);
                return fragmentDel;
            case 3:
                FragmentUpdate fragmentUpdate = new FragmentUpdate();
                fragmentUpdate.setListener(listener);
                return fragmentUpdate;
            default:
                return new FragmentShow();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}