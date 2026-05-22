package com.example.lab_7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab_7.R;

public class MainActivity extends AppCompatActivity {
    EditText editPhone, editFirstName, editLastName;
    Button btnRegistration;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPhone = findViewById(R.id.editPhone);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        btnRegistration = findViewById(R.id.btnRegistration);

        sharedPreferences = getSharedPreferences("TaxiApp", MODE_PRIVATE);

        checkUser();
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFieldsForEmptyValues();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editPhone.addTextChangedListener(textWatcher);
        editFirstName.addTextChangedListener(textWatcher);
        editLastName.addTextChangedListener(textWatcher);

        btnRegistration.setOnClickListener(v -> {
            String phone = editPhone.getText().toString();
            String firstName = editFirstName.getText().toString();
            String lastName = editLastName.getText().toString();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Phone", phone);
            editor.putString("FirstName", firstName);
            editor.putString("LastName", lastName);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, Activity_2.class);
            intent.putExtra("Phone", phone);
            intent.putExtra("FirstName", firstName);
            intent.putExtra("LastName", lastName);
            startActivity(intent);
        });
        Log.d("Lifecycle", "onCreate called in MainActivity");
    }

    private void checkFieldsForEmptyValues() {
        String phone = editPhone.getText().toString();
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();
        btnRegistration.setEnabled(!phone.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty());
    }

    private void checkUser() {
        String phone = sharedPreferences.getString("Phone", null);
        String firstName = sharedPreferences.getString("FirstName", null);
        String lastName = sharedPreferences.getString("LastName", null);

        if (phone != null && firstName != null && lastName != null) {
            btnRegistration.setText("Log in");
            editPhone.setText(phone);
            editFirstName.setText(firstName);
            editLastName.setText(lastName);
        } else {
            btnRegistration.setText("Registration");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Lifecycle", "onStart called in MainActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Lifecycle", "onResume called in MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Lifecycle", "onPause called in MainActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Lifecycle", "onStop called in MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "onDestroy called in MainActivity");
    }
}