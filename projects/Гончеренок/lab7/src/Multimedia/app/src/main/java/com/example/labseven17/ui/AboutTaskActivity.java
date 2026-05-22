package com.example.labseven17.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labseven17.databinding.ActivityAboutTaskBinding;

public class AboutTaskActivity extends AppCompatActivity {

    private ActivityAboutTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
