package com.example.secondlab7taxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity для регистрации и входа пользователя в систему такси.
 * Сохраняет регистрационные данные в SharedPreferences для автоматического входа.
 * При повторном запуске приложения автоматически подставляет сохранённые данные.
 * 
 * Автор: Пекун Марк Сергеевич
 * Группа: АС-66
 * Лабораторная работа №7
 */
public class RegistrationActivity extends AppCompatActivity {

    // Тег для логирования жизненного цикла Activity
    private static final String TAG = "RegistrationActivity";
    
    // Имя файла SharedPreferences для хранения данных пользователя
    private static final String PREFS_NAME = "TaxiAppPrefs";
    
    // Ключи для хранения данных в SharedPreferences
    private static final String KEY_PHONE = "phone";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_IS_REGISTERED = "isRegistered";

    // UI элементы
    private EditText etPhone;        // Поле ввода номера телефона
    private EditText etFirstName;    // Поле ввода имени
    private EditText etLastName;     // Поле ввода фамилии
    private Button btnRegistration;  // Кнопка регистрации/входа
    private TextView tvAboutLink;    // Ссылка на экран "О приложении"
    
    // SharedPreferences для сохранения данных между запусками приложения
    private SharedPreferences prefs;
    
    // Флаг, показывающий зарегистрирован ли пользователь
    private boolean isAlreadyRegistered = false;

    /**
     * Метод onCreate() - точка входа при создании Activity.
     * Инициализирует UI, загружает сохранённые данные и настраивает обработчики.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Создание Activity регистрации");
        
        // Устанавливаем макет интерфейса
        setContentView(R.layout.activity_registration);

        // Инициализация SharedPreferences для работы с сохранёнными данными
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Связываем переменные с элементами интерфейса
        etPhone = findViewById(R.id.etPhone);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        btnRegistration = findViewById(R.id.btnRegistration);
        tvAboutLink = findViewById(R.id.tvAboutLink);

        // Загружаем сохранённые данные пользователя (если есть)
        loadUserData();

        // Обработчик нажатия на кнопку "Регистрация"/"Вход"
        btnRegistration.setOnClickListener(v -> {
            // Проверяем корректность введённых данных
            if (validateInput()) {
                // Сохраняем данные в SharedPreferences
                saveUserData();
                // Переходим на главный экран приложения
                navigateToMainActivity();
            }
        });

        // Обработчик нажатия на ссылку "О приложении / Автор"
        // Открывает экран с информацией о приложении и задании
        tvAboutLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Загрузка ранее сохранённых данных пользователя из SharedPreferences.
     * Если пользователь уже регистрировался, автоматически заполняет поля
     * и меняет текст кнопки с "Registration" на "Log in".
     */
    private void loadUserData() {
        // Проверяем, зарегистрирован ли пользователь
        isAlreadyRegistered = prefs.getBoolean(KEY_IS_REGISTERED, false);
        
        if (isAlreadyRegistered) {
            // Загружаем сохранённые данные
            String phone = prefs.getString(KEY_PHONE, "");
            String firstName = prefs.getString(KEY_FIRST_NAME, "");
            String lastName = prefs.getString(KEY_LAST_NAME, "");

            // Автоматически заполняем поля формы
            etPhone.setText(phone);
            etFirstName.setText(firstName);
            etLastName.setText(lastName);

            // Меняем текст кнопки на "Log in"
            btnRegistration.setText("Log in");
            
            Log.d(TAG, "loadUserData: Загружены данные зарегистрированного пользователя");
        } else {
            // Для нового пользователя оставляем текст "Registration"
            btnRegistration.setText("Registration");
            Log.d(TAG, "loadUserData: Новый пользователь, требуется регистрация");
        }
    }

    /**
     * Проверка корректности введённых данных перед сохранением.
     * Все три поля (телефон, имя, фамилия) обязательны для заполнения.
     * 
     * @return true если все поля заполнены, false если есть пустые поля
     */
    private boolean validateInput() {
        String phone = etPhone.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        // Проверяем поле телефона
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверяем поле имени
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверяем поле фамилии
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Введите фамилию", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Все поля заполнены
        return true;
    }

    /**
     * Сохранение данных пользователя в SharedPreferences.
     * Данные сохраняются локально на устройстве и доступны при следующем запуске.
     */
    private void saveUserData() {
        // Получаем введённые данные
        String phone = etPhone.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();

        // Создаём редактор для записи в SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        
        // Сохраняем все данные пользователя
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putBoolean(KEY_IS_REGISTERED, true);
        
        // Применяем изменения асинхронно
        editor.apply();

        Log.d(TAG, "saveUserData: Данные сохранены - " + firstName + " " + lastName);
    }

    /**
     * Переход на главный экран приложения (MainTaxiActivity).
     * Используется явный Intent с передачей данных пользователя.
     */
    private void navigateToMainActivity() {
        // Создаём явный Intent для запуска MainTaxiActivity
        Intent intent = new Intent(RegistrationActivity.this, MainTaxiActivity.class);
        
        // Передаём данные пользователя через Intent (extras)
        intent.putExtra(KEY_PHONE, etPhone.getText().toString().trim());
        intent.putExtra(KEY_FIRST_NAME, etFirstName.getText().toString().trim());
        intent.putExtra(KEY_LAST_NAME, etLastName.getText().toString().trim());
        
        // Запускаем новый Activity
        startActivity(intent);
        
        Log.d(TAG, "navigateToMainActivity: Переход на главный экран приложения");
    }

    // ==================== МЕТОДЫ ЖИЗНЕННОГО ЦИКЛА ACTIVITY ====================
    // Логирование для отслеживания порядка вызова методов жизненного цикла
    
    /**
     * onStart() - Activity становится видимым на экране.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity становится видимым");
    }

    /**
     * onResume() - Activity активен и готов к взаимодействию с пользователем.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity активен, пользователь может взаимодействовать");
    }

    /**
     * onPause() - Activity теряет фокус (например, поверх него появился другой Activity).
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity теряет фокус");
    }

    /**
     * onStop() - Activity полностью скрыт и не виден пользователю.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity больше не виден");
    }

    /**
     * onDestroy() - Activity уничтожается системой или завершается пользователем.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity уничтожается");
    }

    /**
     * onRestart() - Activity перезапускается после того как был остановлен (onStop).
     * Вызывается перед onStart().
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Activity перезапускается после остановки");
    }
}
