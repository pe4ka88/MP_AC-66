package com.example.secondlab7taxi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Главный экран приложения "Такси".
 * Этот Activity отображает информацию о пользователе (имя и телефон),
 * позволяет выбрать маршрут поездки и вызвать такси.
 * 
 * Автор: Пекун Марк Сергеевич
 * Группа: АС-66
 * Лабораторная работа №7
 */
public class MainTaxiActivity extends AppCompatActivity {

    // Тег для логирования, используется для отслеживания жизненного цикла Activity
    private static final String TAG = "MainTaxiActivity";
    
    // Код запроса для получения результата от RouteActivity
    private static final int REQUEST_CODE_ROUTE = 100;

    // UI элементы для отображения информации
    private TextView tvUserName;    // Отображает имя и фамилию пользователя
    private TextView tvUserPhone;   // Отображает номер телефона пользователя
    private TextView tvRoute;       // Отображает выбранный маршрут
    private Button btnSetPath;      // Кнопка для выбора маршрута
    private Button btnCallTaxi;     // Кнопка для вызова такси
    
    // Переменная для хранения информации о маршруте
    private String routeInfo = "";

    /**
     * Метод onCreate() вызывается при создании Activity.
     * Здесь происходит инициализация UI элементов и настройка обработчиков событий.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity создаётся");
        
        // Устанавливаем layout для этого Activity
        setContentView(R.layout.activity_main_taxi);

        // Инициализация всех UI элементов через findViewById
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvRoute = findViewById(R.id.tvRoute);
        btnSetPath = findViewById(R.id.btnSetPath);
        btnCallTaxi = findViewById(R.id.btnCallTaxi);

        // Загружаем данные пользователя, переданные через Intent
        loadUserDataFromIntent();

        // По умолчанию кнопка "Вызвать такси" отключена до выбора маршрута
        btnCallTaxi.setEnabled(false);

        // Обработчик нажатия на кнопку "Выбрать маршрут"
        btnSetPath.setOnClickListener(v -> {
            Log.d(TAG, "btnSetPath: Пользователь нажал кнопку выбора маршрута");
            
            // Создаём неявный Intent для запуска RouteActivity
            // Используем action для демонстрации неявных Intent
            Intent intent = new Intent("com.example.secondlab7taxi.ACTION_SET_ROUTE");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            
            // Запускаем Activity с ожиданием результата
            startActivityForResult(intent, REQUEST_CODE_ROUTE);
        });

        // Обработчик нажатия на кнопку "Вызвать такси"
        btnCallTaxi.setOnClickListener(v -> {
            Log.d(TAG, "btnCallTaxi: Пользователь вызывает такси");
            
            // Показываем красивое уведомление об успешном вызове такси
            showTaxiCalledNotification();
        });
    }

    /**
     * Метод для отображения красивого уведомления о вызове такси.
     * Вместо стандартного Toast с иконкой Android, показываем сообщение с иконкой такси.
     */
    private void showTaxiCalledNotification() {
        // Создаём Toast с более длительным временем показа
        Toast toast = Toast.makeText(this, "🚖 Такси успешно вызвано!\n" +
                "Водитель уже едет к вам", Toast.LENGTH_LONG);
        
        // Позиционируем Toast в центре экрана для лучшей видимости
        toast.setGravity(Gravity.CENTER, 0, 0);
        
        // Отображаем Toast
        toast.show();
        
        Log.d(TAG, "showTaxiCalledNotification: Уведомление о вызове такси показано");
    }

    /**
     * Загрузка данных пользователя из Intent.
     * Эти данные передаются из RegistrationActivity при регистрации/входе.
     */
    private void loadUserDataFromIntent() {
        Intent intent = getIntent();
        
        if (intent != null) {
            // Получаем переданные данные из Intent
            String firstName = intent.getStringExtra("firstName");
            String lastName = intent.getStringExtra("lastName");
            String phone = intent.getStringExtra("phone");

            // Проверяем и отображаем имя и фамилию
            if (firstName != null && lastName != null) {
                tvUserName.setText(String.format("%s %s", firstName, lastName));
                Log.d(TAG, "loadUserDataFromIntent: Имя загружено - " + firstName + " " + lastName);
            }

            // Проверяем и отображаем телефон
            if (phone != null) {
                tvUserPhone.setText(String.format("Телефон: %s", phone));
                Log.d(TAG, "loadUserDataFromIntent: Телефон загружен - " + phone);
            }
        }
    }

    /**
     * Метод onActivityResult() вызывается когда RouteActivity возвращает результат.
     * Здесь мы получаем данные о выбранном маршруте.
     * 
     * @param requestCode Код запроса, который мы передали в startActivityForResult()
     * @param resultCode Результат: RESULT_OK или RESULT_CANCELED
     * @param data Intent с данными от RouteActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        // Проверяем, что это результат от выбора маршрута
        if (requestCode == REQUEST_CODE_ROUTE) {
            
            // Если пользователь успешно выбрал маршрут
            if (resultCode == RESULT_OK && data != null) {
                
                // Извлекаем все параметры маршрута из Intent
                String from = data.getStringExtra("from");
                String to = data.getStringExtra("to");
                String street = data.getStringExtra("street");
                String house = data.getStringExtra("house");
                String comment = data.getStringExtra("comment");
                String additional = data.getStringExtra("additional");

                // Формируем читаемый текст маршрута
                routeInfo = buildRouteInfo(from, to, street, house, comment, additional);
                
                // Отображаем маршрут на экране
                tvRoute.setText(routeInfo);

                // Активируем кнопку вызова такси
                btnCallTaxi.setEnabled(true);

                Log.d(TAG, "onActivityResult: Маршрут успешно получен");
                
            } else if (resultCode == RESULT_CANCELED) {
                // Если пользователь отменил выбор маршрута
                Log.d(TAG, "onActivityResult: Выбор маршрута отменён пользователем");
                Toast.makeText(this, "Выбор маршрута отменён", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Формирование текстового представления маршрута.
     * Метод собирает все непустые поля маршрута в читаемую строку.
     * 
     * @param from Откуда едем
     * @param to Куда едем
     * @param street Улица назначения
     * @param house Номер дома
     * @param comment Комментарий к поездке
     * @param additional Дополнительная информация
     * @return Отформатированная строка с информацией о маршруте
     */
    private String buildRouteInfo(String from, String to, String street, 
                                   String house, String comment, String additional) {
        StringBuilder sb = new StringBuilder();
        sb.append("📍 Маршрут:\n");

        // Добавляем каждое поле только если оно не пустое
        if (from != null && !from.isEmpty()) {
            sb.append("Откуда: ").append(from).append("\n");
        }

        if (to != null && !to.isEmpty()) {
            sb.append("Куда: ").append(to).append("\n");
        }

        if (street != null && !street.isEmpty()) {
            sb.append("Улица: ").append(street).append("\n");
        }

        if (house != null && !house.isEmpty()) {
            sb.append("Дом: ").append(house).append("\n");
        }

        if (comment != null && !comment.isEmpty()) {
            sb.append("Комментарий: ").append(comment).append("\n");
        }

        if (additional != null && !additional.isEmpty()) {
            sb.append("Доп. инфо: ").append(additional).append("\n");
        }

        return sb.toString();
    }

    // ==================== МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА ACTIVITY ====================
    // Эти методы отслеживают состояние Activity и логируют все изменения
    
    /**
     * onStart() вызывается когда Activity становится видимым для пользователя.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity становится видимым");
    }

    /**
     * onResume() вызывается когда Activity получает фокус и готов к взаимодействию.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity на переднем плане, готов к взаимодействию");
    }

    /**
     * onPause() вызывается когда Activity теряет фокус.
     * Может быть вызван при переходе к другому Activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity теряет фокус");
    }

    /**
     * onStop() вызывается когда Activity больше не виден пользователю.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity больше не видим пользователю");
    }

    /**
     * onDestroy() вызывается перед уничтожением Activity.
     * Здесь следует освобождать ресурсы.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity уничтожается");
    }

    /**
     * onRestart() вызывается когда Activity перезапускается после onStop().
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Activity перезапускается после остановки");
    }
}
