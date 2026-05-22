package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etPhone, etFirstName, etLastName;
    private Button btnRegister, btnShowTask;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("LIFECYCLE", "MainActivity onCreate");

        etPhone = findViewById(R.id.etPhone);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnRegister = findViewById(R.id.btnRegister);
        btnShowTask = findViewById(R.id.btnShowTask);

        prefs = getSharedPreferences("user", MODE_PRIVATE);

        // Восстановление данных
        String savedPhone = prefs.getString("phone", "");
        String savedFirst = prefs.getString("first", "");
        String savedLast = prefs.getString("last", "");

        if (!savedPhone.isEmpty()) {
            etPhone.setText(savedPhone);
            etFirstName.setText(savedFirst);
            etLastName.setText(savedLast);
            btnRegister.setText("Log in");
        }

        btnRegister.setOnClickListener(v -> {
            String phone = etPhone.getText().toString();
            String first = etFirstName.getText().toString();
            String last = etLastName.getText().toString();

            // Сохраняем данные
            prefs.edit()
                    .putString("phone", phone)
                    .putString("first", first)
                    .putString("last", last)
                    .apply();

            // Явный вызов второго Activity
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            intent.putExtra("phone", phone);
            intent.putExtra("first", first);
            intent.putExtra("last", last);
            startActivity(intent);
        });

        // Кнопка "Нажимает Горобец"
        btnShowTask.setOnClickListener(v -> {
            String task = "1. Разработать приложение Taxi, состоящее из трех Activity.\n" +
                    "2. В первом Activity создать три редактируемых текстовых поля (EditText) для ввода пользователем регистрационных данных (телефон, имя и фамилия) и кнопку Registration для запуска второго Activity.\n" +
                    "3. При нажатии кнопки Registration выполнить явный вызов второго Activity с передачей данных о пользователе (телефон, имя и фамилия).\n" +
                    "4. Во втором Activity создать два текстовых поля (TextView) для вывода переданной информации о пользователе (имя и фамилия, телефон), пустое по умолчанию текстовое поле (TextView) для вывода маршрута движения, кнопку Set path для ввода этого маршрута, кнопку вызова такси Call Taxi (недоступна, пока не введен маршрут движения).\n" +
                    "5. При нажатии кнопки Set path выполнить неявный вызов третьего Activity с помощью метода startActivityForResult.\n" +
                    "6. В третьем Activity создать шесть редактируемых текстовых полей (EditText) для ввода параметров маршрута движения, кнопку OК для возврата во второе Activity.\n" +
                    "7. При нажатии кнопки ОК реализовать возврат во второе Activity с передачей в качестве результата параметров маршрута движения.\n" +
                    "8. После возврата во второе Activity в текстовое поле вывести информацию о маршруте движения и предложение вызвать такси, кнопку вызова такси Call taxi сделать доступной.\n" +
                    "9. При нажатии кнопки Call Taxi вывести всплывающее сообщение об успешной отправке такси.\n" +
                    "10. Реализовать сохранение регистрационных данных пользователя в исходном Activity с помощью класса SharedPreferences и восстанавливать эту информацию при повторных запусках приложения. При этом название кнопки Registration должно программно меняться на Log in.\n" +
                    "11. Вывести в лог очередность вызовов методов жизненного цикла первого, второго и третьего Activity.\n" +
                    "12. Сделать вывод на основании логов о жизненном цикле Activity.\n" +
                    "13. Продемонстрировать работу приложения Taxi на эмуляторе или реальном устройстве.\n" +
                    "14. Дополнительное задание: реализовать функционал геолокации.";

            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Задание")
                    .setMessage(task)
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
        });
    }

    @Override protected void onStart() { super.onStart(); Log.d("LIFECYCLE", "MainActivity onStart"); }
    @Override protected void onResume() { super.onResume(); Log.d("LIFECYCLE", "MainActivity onResume"); }
    @Override protected void onPause() { super.onPause(); Log.d("LIFECYCLE", "MainActivity onPause"); }
    @Override protected void onStop() { super.onStop(); Log.d("LIFECYCLE", "MainActivity onStop"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d("LIFECYCLE", "MainActivity onDestroy"); }
}
