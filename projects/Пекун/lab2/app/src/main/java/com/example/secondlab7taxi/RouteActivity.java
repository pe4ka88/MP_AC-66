package com.example.secondlab7taxi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity для выбора и настройки параметров маршрута поездки.
 * Пользователь вводит адреса отправления и назначения, а также дополнительную информацию.
 * После заполнения данные возвращаются в MainTaxiActivity через setResult().
 * 
 * Запускается через неявный Intent с помощью метода startActivityForResult().
 * 
 * Автор: Пекун Марк Сергеевич
 * Группа: АС-66
 * Лабораторная работа №7
 */
public class RouteActivity extends AppCompatActivity {

    // Тег для логирования жизненного цикла Activity
    private static final String TAG = "RouteActivity";

    // UI элементы для ввода параметров маршрута
    private EditText etFrom;        // Откуда едем (обязательное поле)
    private EditText etTo;          // Куда едем (обязательное поле)
    private EditText etStreet;      // Улица назначения (необязательное поле)
    private EditText etHouse;       // Номер дома (необязательное поле)
    private EditText etComment;     // Комментарий для водителя (необязательное поле)
    private EditText etAdditional;  // Дополнительная информация (необязательное поле)
    private Button btnOk;           // Кнопка подтверждения маршрута

    /**
     * Метод onCreate() - инициализация Activity при создании.
     * Настраивает UI и обработчики событий для кнопок.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Создание Activity выбора маршрута");
        
        // Устанавливаем макет интерфейса
        setContentView(R.layout.activity_route);

        // Связываем переменные с элементами интерфейса
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        etStreet = findViewById(R.id.etStreet);
        etHouse = findViewById(R.id.etHouse);
        etComment = findViewById(R.id.etComment);
        etAdditional = findViewById(R.id.etAdditional);
        btnOk = findViewById(R.id.btnOk);

        // Обработчик нажатия на кнопку "Подтвердить маршрут"
        btnOk.setOnClickListener(v -> {
            // Проверяем корректность введённых данных
            if (validateInput()) {
                // Возвращаем данные маршрута в вызывающий Activity
                returnRouteData();
            }
        });

        // Обработка системной кнопки "Назад"
        // Современный подход для Android API 33+ (заменяет устаревший onBackPressed)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "handleOnBackPressed: Пользователь отменил выбор маршрута");
                
                // Устанавливаем результат RESULT_CANCELED
                setResult(RESULT_CANCELED);
                
                // Закрываем Activity
                finish();
            }
        });
    }

    /**
     * Проверка обязательных полей перед возвратом данных.
     * Поля "Откуда" и "Куда" обязательны для заполнения.
     * Остальные поля опциональны.
     * 
     * @return true если обязательные поля заполнены, false в противном случае
     */
    private boolean validateInput() {
        String from = etFrom.getText().toString().trim();
        String to = etTo.getText().toString().trim();

        // Проверяем поле "Откуда"
        if (from.isEmpty()) {
            Toast.makeText(this, "Укажите откуда едете", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверяем поле "Куда"
        if (to.isEmpty()) {
            Toast.makeText(this, "Укажите куда едете", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Все обязательные поля заполнены
        return true;
    }

    /**
     * Возврат данных маршрута в вызывающий Activity (MainTaxiActivity).
     * Создаёт Intent с данными и устанавливает результат RESULT_OK.
     * После этого Activity закрывается, и управление возвращается в MainTaxiActivity.
     */
    private void returnRouteData() {
        // Создаём Intent для возврата результата
        Intent resultIntent = new Intent();
        
        // Добавляем все параметры маршрута в Intent
        // Все значения обрезаются от пробелов методом trim()
        resultIntent.putExtra("from", etFrom.getText().toString().trim());
        resultIntent.putExtra("to", etTo.getText().toString().trim());
        resultIntent.putExtra("street", etStreet.getText().toString().trim());
        resultIntent.putExtra("house", etHouse.getText().toString().trim());
        resultIntent.putExtra("comment", etComment.getText().toString().trim());
        resultIntent.putExtra("additional", etAdditional.getText().toString().trim());

        // Устанавливаем результат выполнения как успешный (RESULT_OK)
        // Это позволит MainTaxiActivity понять, что маршрут выбран
        setResult(RESULT_OK, resultIntent);
        
        Log.d(TAG, "returnRouteData: Данные маршрута успешно возвращены");
        
        // Закрываем Activity и возвращаемся в MainTaxiActivity
        finish();
    }

    // ==================== МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА ACTIVITY ====================
    // Логирование для изучения порядка вызова методов жизненного цикла
    
    /**
     * onStart() - Activity становится видимым пользователю.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity выбора маршрута становится видимым");
    }

    /**
     * onResume() - Activity активен и находится на переднем плане.
     * Пользователь может взаимодействовать с интерфейсом.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity активен, готов к вводу данных");
    }

    /**
     * onPause() - Activity теряет фокус.
     * Может быть вызван когда появляется другой Activity или диалог.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity теряет фокус");
    }

    /**
     * onStop() - Activity больше не виден пользователю.
     * Вызывается когда пользователь возвращается в предыдущий Activity.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity скрыт от пользователя");
    }

    /**
     * onDestroy() - Activity уничтожается.
     * Вызывается после finish() или когда система завершает Activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity выбора маршрута уничтожается");
    }

    /**
     * onRestart() - Activity перезапускается после onStop().
     * Вызывается перед onStart() когда Activity снова становится видимым.
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Activity перезапускается");
    }
}
