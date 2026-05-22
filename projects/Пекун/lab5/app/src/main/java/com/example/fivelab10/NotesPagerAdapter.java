package com.example.fivelab10;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NotesPagerAdapter extends FragmentStateAdapter {

    private final String[] pageTitles = {"Show", "Add", "Del", "Update"};
    private final FragmentShow fragmentShow = new FragmentShow();
    private final FragmentAdd fragmentAdd = new FragmentAdd();
    private final FragmentDel fragmentDel = new FragmentDel();
    private final FragmentUpdate fragmentUpdate = new FragmentUpdate();

    public NotesPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return fragmentShow;
            case 1:
                return fragmentAdd;
            case 2:
                return fragmentDel;
            case 3:
                return fragmentUpdate;
            default:
                return fragmentShow;
        }
    }

    @Override
    public int getItemCount() {
        return pageTitles.length;
    }

    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }

    public FragmentShow getFragmentShow() {
        return fragmentShow;
    }
}
