package com.example.labseven17.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.labseven17.databinding.ActivityAboutAuthorBinding;

public class AboutAuthorActivity extends AppCompatActivity {

    private ActivityAboutAuthorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutAuthorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
