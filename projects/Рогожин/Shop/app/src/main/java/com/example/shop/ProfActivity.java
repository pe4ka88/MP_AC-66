package com.example.shop;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shop.user.UserDatabase;
import com.example.shop.user.UserSession;

public class ProfActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword, editAddress;
    Button btnLogin, btnLogout, btnSaveAddress;
    LinearLayout profileLayout;
    TextView textProfileName, textProfileEmail, textBalance;

    UserDatabase dbHelper;
    SQLiteDatabase db;

    String currentEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editAddress = findViewById(R.id.editAddress);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);

        profileLayout = findViewById(R.id.profileLayout);

        textProfileName = findViewById(R.id.textProfileName);
        textProfileEmail = findViewById(R.id.textProfileEmail);
        textBalance = findViewById(R.id.textBalance);

        dbHelper = new UserDatabase(this);
        db = dbHelper.getWritableDatabase();

        checkLogin();

        btnLogin.setOnClickListener(v -> login());
        btnLogout.setOnClickListener(v -> logout());
        btnSaveAddress.setOnClickListener(v -> saveAddress());
    }

    void login() {

        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email=?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {

            String savedPassword = cursor.getString(
                    cursor.getColumnIndexOrThrow("password"));

            if (!savedPassword.equals(password)) {
                Toast.makeText(this, "Неверный пароль", Toast.LENGTH_LONG).show();
                cursor.close();
                return;
            }

            loadProfile(cursor);

        } else {

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("email", email);
            values.put("password", password);
            values.put("balance", 100);

            db.insert("users", null, values);

            Cursor newCursor = db.rawQuery(
                    "SELECT * FROM users WHERE email=?",
                    new String[]{email}
            );

            if (newCursor.moveToFirst()) {
                loadProfile(newCursor);
            }

            newCursor.close();
        }

        cursor.close();
    }

    void loadProfile(Cursor cursor) {

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));
        String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));

        currentEmail = email;

        // сохраняем сессию
        UserSession.setUser(this, email);

        profileLayout.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);

        editName.setVisibility(View.GONE);
        editEmail.setVisibility(View.GONE);
        editPassword.setVisibility(View.GONE);

        textProfileName.setText(name);
        textProfileEmail.setText(email);

        double ordersSum = getOrdersSum(email);
        double realBalance = balance - ordersSum;

        textBalance.setText("$" + realBalance);

        if (address != null) {
            editAddress.setText(address);
        }
    }

    double getOrdersSum(String email) {

        double sum = 0;

        Cursor cursor = db.rawQuery(
                "SELECT SUM(price) FROM orders WHERE user_email=?",
                new String[]{email}
        );

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }

        cursor.close();
        return sum;
    }

    void saveAddress() {

        String address = editAddress.getText().toString();

        ContentValues values = new ContentValues();
        values.put("address", address);

        db.update(
                "users",
                values,
                "email=?",
                new String[]{currentEmail}
        );

        Toast.makeText(this, "Адрес сохранён", Toast.LENGTH_SHORT).show();
    }

    void checkLogin() {

        String savedUser = UserSession.getUser(this);

        if (savedUser == null) return;

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email=?",
                new String[]{savedUser}
        );

        if (cursor.moveToFirst()) {
            loadProfile(cursor);
        }

        cursor.close();
    }

    void logout() {

        UserSession.logout(this);

        currentEmail = null;

        profileLayout.setVisibility(View.GONE);

        editName.setVisibility(View.VISIBLE);
        editEmail.setVisibility(View.VISIBLE);
        editPassword.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);

        editName.setText("");
        editEmail.setText("");
        editPassword.setText("");
        editAddress.setText("");

        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
    }
}