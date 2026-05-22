package com.example.lab4_10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NotesPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TITLES = {"Show", "Add", "Del", "Update"};

    private final FragmentShow fragmentShow = new FragmentShow();
    private final FragmentAdd fragmentAdd = new FragmentAdd();
    private final FragmentDel fragmentDel = new FragmentDel();
    private final FragmentUpdate fragmentUpdate = new FragmentUpdate();

    public NotesPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return fragmentShow;
            case 1: return fragmentAdd;
            case 2: return fragmentDel;
            case 3: return fragmentUpdate;
            default: return fragmentShow;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    public FragmentShow getFragmentShow() {
        return fragmentShow;
    }
}
