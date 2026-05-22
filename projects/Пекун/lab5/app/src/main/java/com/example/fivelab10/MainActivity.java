package com.example.fivelab10;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements NoteActionListener {

    private NotesPagerAdapter notesPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper dbHelper = new DBHelper(this);
        dbHelper.ensureMinimumNotes();

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        notesPagerAdapter = new NotesPagerAdapter(this);
        viewPager.setAdapter(notesPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> tab.setText(notesPagerAdapter.getPageTitle(position))
        ).attach();
    }

    @Override
    public void onNotesChanged() {
        FragmentShow fragmentShow = notesPagerAdapter.getFragmentShow();
        if (fragmentShow != null) {
            fragmentShow.refreshNotes();
        }
    }
}