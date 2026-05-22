package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "LIFECYCLE";

    EditText et1, et2, et3, et4, et5, et6;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ThirdActivity -> onCreate");
        setContentView(R.layout.activity_third);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);
        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            Log.d(TAG, "ThirdActivity -> OK clicked");

            String path = et1.getText().toString() + " " +
                    et2.getText().toString() + ", " +
                    et3.getText().toString() + " -> " +
                    et4.getText().toString() + " " +
                    et5.getText().toString() + ", " +
                    et6.getText().toString();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("path", path);

            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "ThirdActivity -> onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "ThirdActivity -> onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d(TAG, "ThirdActivity -> onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "ThirdActivity -> onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "ThirdActivity -> onDestroy"); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "ThirdActivity -> onRestart"); }
}