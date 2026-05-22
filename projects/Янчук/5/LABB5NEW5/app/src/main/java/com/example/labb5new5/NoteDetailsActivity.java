package com.example.labb5new5;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoteDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvText = findViewById(R.id.tvText);

        String title = getIntent().getStringExtra("title");
        String text = getIntent().getStringExtra("text");

        tvTitle.setText(title);
        tvText.setText(text);
    }
}
