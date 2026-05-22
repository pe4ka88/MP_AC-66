package com.example.taxi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView tvContent = findViewById(R.id.tvHistoryContent);
        Button btnBack = findViewById(R.id.btnBackHistory);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String history = prefs.getString("history", "");

        if (history.isEmpty()) {
            tvContent.setText(R.string.no_history);
        } else {
            tvContent.setText(history);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
