package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ArrayList<String> items = getIntent().getStringArrayListExtra("items");
        TextView tvInfo = findViewById(R.id.tvInfo);
        tvInfo.setText("В Вашей корзине " + items.size() + " товара(ов):");

        ListView lvSecond = findViewById(R.id.lvSecond);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvSecond.setAdapter(adapter);
    }
}
