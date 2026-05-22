package com.example.lr5;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NotesPagerAdapter extends FragmentPagerAdapter {

    private final String[] titles = {"Show", "Add", "Del", "Update"};
    private final FragmentShow fragmentShow;
    private final FragmentAdd fragmentAdd;
    private final FragmentDel fragmentDel;
    private final FragmentUpdate fragmentUpdate;

    public NotesPagerAdapter(@NonNull FragmentManager fm, NotesDbHelper dbHelper) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentShow = FragmentShow.newInstance(dbHelper);
        fragmentAdd = FragmentAdd.newInstance(dbHelper);
        fragmentDel = FragmentDel.newInstance(dbHelper);
        fragmentUpdate = FragmentUpdate.newInstance(dbHelper);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return fragmentShow;
            case 1:
                return fragmentAdd;
            case 2:
                return fragmentDel;
            default:
                return fragmentUpdate;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public void refreshShowFragment() {
        if (fragmentShow != null) {
            fragmentShow.reloadNotes();
        }
    }
}
