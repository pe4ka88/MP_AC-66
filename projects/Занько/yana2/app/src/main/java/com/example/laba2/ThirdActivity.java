package com.example.laba2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "L3_Third";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_third);

        EditText etFrom = findViewById(R.id.etFrom);
        EditText etTo = findViewById(R.id.etTo);
        EditText etDate = findViewById(R.id.etDate);
        EditText etTime = findViewById(R.id.etTime);
        EditText etClass = findViewById(R.id.etClass);
        EditText etComment = findViewById(R.id.etComment);

        Button btnOk = findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            String from = etFrom.getText().toString().trim();
            String to = etTo.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();
            String cls = etClass.getText().toString().trim();
            String cmt = etComment.getText().toString().trim();

            if (from.isEmpty() || to.isEmpty()) {
                Toast.makeText(this, "Заполните 'Откуда' и 'Куда'", Toast.LENGTH_SHORT).show();
                return;
            }

            String route = "Откуда: " + from +
                    "; Куда: " + to +
                    (date.isEmpty() ? "" : "; Дата: " + date) +
                    (time.isEmpty() ? "" : "; Время: " + time) +
                    (cls.isEmpty() ? "" : "; Класс: " + cls) +
                    (cmt.isEmpty() ? "" : "; Комментарий: " + cmt);

            Intent result = new Intent();
            result.putExtra("route", route);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    @Override protected void onStart() { super.onStart(); Log.d(TAG, "onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d(TAG, "onResume"); }
    @Override protected void onPause() { Log.d(TAG, "onPause"); super.onPause(); }
    @Override protected void onStop() { Log.d(TAG, "onStop"); super.onStop(); }
    @Override protected void onRestart() { super.onRestart(); Log.d(TAG, "onRestart"); }
    @Override protected void onDestroy() { Log.d(TAG, "onDestroy"); super.onDestroy(); }
}
