package com.example.taxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editTextPhone, editTextName, editTextSurname;
    private Button buttonRegister, buttonInfo;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "Lifecycle_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);

        editTextPhone = findViewById(R.id.editTextPhone);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonInfo = findViewById(R.id.buttonInfo);

        loadUserData();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editTextPhone.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String surname = editTextSurname.getText().toString().trim();

                if (phone.isEmpty() || name.isEmpty() || surname.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveUserData();
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("name", name);
                intent.putExtra("surname", surname);
                startActivity(intent);
            }
        });

        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });
    }

    private void saveUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", editTextPhone.getText().toString());
        editor.putString("name", editTextName.getText().toString());
        editor.putString("surname", editTextSurname.getText().toString());
        editor.apply();
    }

    private void loadUserData() {
        String phone = sharedPreferences.getString("phone", "");
        String name = sharedPreferences.getString("name", "");
        String surname = sharedPreferences.getString("surname", "");

        if (!phone.isEmpty() && !name.isEmpty() && !surname.isEmpty()) {
            editTextPhone.setText(phone);
            editTextName.setText(name);
            editTextSurname.setText(surname);
            buttonRegister.setText(getString(R.string.login));
        }
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_info, null);
        builder.setView(view);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }
}