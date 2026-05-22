package com.example.memorygamejava;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CookiePolicyActivity extends AppCompatActivity {

    private TextView cookieTextView;
    private ProgressBar progressBar;
    private Button refreshButton;
    private Button acceptButton;

    private void applyTheme() {
        // Получаем сохраненную тему
        android.content.SharedPreferences prefs = getSharedPreferences("GameSettings", MODE_PRIVATE);
        int themeIndex = prefs.getInt("theme", 0);

        // Применяем тему к корневому View
        android.view.View rootView = getWindow().getDecorView().getRootView();
        if (rootView == null) return;

        switch (themeIndex) {
            case 1: // светло-фиолетовый
                rootView.setBackgroundColor(getResources().getColor(R.color.light_purple));
                break;
            case 2: // светло-голубой
                rootView.setBackgroundColor(getResources().getColor(R.color.light_blue));
                break;
            case 3: // светло-зеленый
                rootView.setBackgroundColor(getResources().getColor(R.color.light_green));
                break;
            case 4: // светло-серый
                rootView.setBackgroundColor(getResources().getColor(R.color.light_gray));
                break;
            default: // белый
                rootView.setBackgroundColor(getResources().getColor(android.R.color.white));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookie_policy);
        // Применяем тему
        applyTheme();

        cookieTextView = findViewById(R.id.cookieTextView);
        progressBar = findViewById(R.id.progressBar);
        refreshButton = findViewById(R.id.refreshButton);
        acceptButton = findViewById(R.id.acceptButton);

        // Загружаем правила куки из API
        loadCookiePolicy();

        refreshButton.setOnClickListener(v -> loadCookiePolicy());

        acceptButton.setOnClickListener(v -> {
            // Сохраняем согласие пользователя
            getSharedPreferences("AppSettings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("cookies_accepted", true)
                    .apply();

            Toast.makeText(this, "Согласие на использование куки сохранено", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadCookiePolicy() {
        new FetchCookiePolicyTask().execute();
    }

    private class FetchCookiePolicyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            cookieTextView.setText("Загрузка правил использования куки...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Используем публичное API для получения данных (пример)
                // В реальном приложении используйте ваш собственный endpoint
                URL url = new URL("https://jsonplaceholder.typicode.com/posts/1");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Парсим JSON
                JSONObject json = new JSONObject(response.toString());

                // Формируем текст правил куки
                return generateCookiePolicyText(json);

            } catch (Exception e) {
                return "Ошибка загрузки: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            cookieTextView.setText(result);
        }
    }

    private String generateCookiePolicyText(JSONObject json) {
        // Создаем форматтер для времени
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault());
        String currentTime = timeFormat.format(new java.util.Date());

        return " ПОЛИТИКА ИСПОЛЬЗОВАНИЯ COOKIE \n\n" +
                "Приложение Memory Game ЛР1 использует cookie для:\n\n" +
                "1.  Сохранения игровых рекордов\n" +
                "2.  Запоминания настроек игры\n" +
                "3.  Идентификации пользователя\n" +
                "4.  Отслеживания времени сессии\n\n" +
                " Ваши данные защищены и не передаются третьим лицам\n" +
                " Вы можете отозвать согласие в любое время\n" +
                " Приложение разработано: Савинец М. Д. (АС-66)\n\n" +
                "API Response ID: " + json.optString("id", "N/A") + "\n" +
                "Загружено: " + currentTime;
    }
}