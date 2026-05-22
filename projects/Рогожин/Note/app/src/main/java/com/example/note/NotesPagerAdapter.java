package com.example.note;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.note.fragments.*;

public class NotesPagerAdapter extends FragmentPagerAdapter {

    public NotesPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentShow();
            case 1: return new FragmentAdd();
            case 2: return new FragmentDel();
            case 3: return new FragmentUpdate();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Show";
            case 1: return "Add";
            case 2: return "Del";
            case 3: return "Update";
        }
        return "";
    }
}
