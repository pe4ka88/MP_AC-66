package com.example.taxi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvPhone = findViewById(R.id.tvProfilePhone);
        Button btnBack = findViewById(R.id.btnBackProfile);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String firstName = prefs.getString("firstName", "");
        String lastName = prefs.getString("lastName", "");
        String phone = prefs.getString("phone", "");

        tvName.setText(firstName + " " + lastName);
        tvPhone.setText(phone);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
