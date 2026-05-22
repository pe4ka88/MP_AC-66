package com.example.geo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AnalyticsPagerAdapter extends FragmentStateAdapter {

    public AnalyticsPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {

            case 0:
                return new DayFragment();

            case 1:
                return new StatsFragment();

            case 2:
                return new PlacesFragment();

            default:
                return new StatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}