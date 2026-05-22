package com.example.mynotes;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = DatabaseHelper.getInstance(this);

        int notesCount = databaseHelper.getNotesCount();
        Toast.makeText(this, "База данных содержит " + notesCount + " заметок", Toast.LENGTH_LONG).show();

        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        viewPagerAdapter.addFragment(new FragmentShow(), "Show");
        viewPagerAdapter.addFragment(new FragmentAdd(), "Add");
        viewPagerAdapter.addFragment(new FragmentDel(), "Del");
        viewPagerAdapter.addFragment(new FragmentUpdate(), "Update");
        viewPagerAdapter.notifyDataSetChanged();
    }
}