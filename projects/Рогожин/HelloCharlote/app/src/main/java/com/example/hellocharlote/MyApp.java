package com.example.hellocharlote;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this); // инициализация Firebase
        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);
    }
}