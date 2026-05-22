package com.example.laba2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AuthorActivity extends AppCompatActivity {

    private static final String TAG = "L_Author";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_author);

        Button btnPress = findViewById(R.id.btnPress);
        Button btnBack  = findViewById(R.id.btnBack);

        btnPress.setOnClickListener(v ->
                Toast.makeText(this, "Нажимает Занько ", Toast.LENGTH_SHORT).show()
        );

        btnBack.setOnClickListener(v -> finish());
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { Log.d(TAG, "onPause"); super.onPause(); }
    @Override protected void onStop() { Log.d(TAG, "onStop"); super.onStop(); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { Log.d(TAG, "onDestroy"); super.onDestroy(); }
}
