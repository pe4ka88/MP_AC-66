package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);


        final View blur = findViewById(R.id.blurOverlay);
        final Button startBtn = findViewById(R.id.btnStartGame);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (blur != null && startBtn != null) {
                    blur.animate().alpha(1.0f).setDuration(1000);

                    startBtn.setVisibility(View.VISIBLE);
                    startBtn.animate().alpha(1.0f).setDuration(1000);
                }
            }
        }, 2000);


        if (startBtn != null) {
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.content.Intent intent = new android.content.Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

}
