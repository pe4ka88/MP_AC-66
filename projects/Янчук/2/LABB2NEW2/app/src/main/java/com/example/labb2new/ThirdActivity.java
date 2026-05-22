package com.example.labb2new;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class ThirdActivity extends AppCompatActivity {

    EditText et1, et2, et3, et4, et5, et6;
    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        Log.d("LIFECYCLE", "ThirdActivity: onCreate");

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);
        btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            String route = et1.getText() + ", " +
                    et2.getText() + ", " +
                    et3.getText() + ", " +
                    et4.getText() + ", " +
                    et5.getText() + ", " +
                    et6.getText();

            Intent result = new Intent();
            result.putExtra("route", route);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    @Override protected void onStart() { super.onStart(); Log.d("LIFECYCLE", "ThirdActivity: onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d("LIFECYCLE", "ThirdActivity: onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d("LIFECYCLE", "ThirdActivity: onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d("LIFECYCLE", "ThirdActivity: onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d("LIFECYCLE", "ThirdActivity: onDestroy"); }
}
