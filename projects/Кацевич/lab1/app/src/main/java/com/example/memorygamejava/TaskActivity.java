package com.example.memorygamejava;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_task);
        // Применяем тему
        applyTheme();

        TextView taskText = findViewById(R.id.taskText);
        Button backButton = findViewById(R.id.backButton);

        taskText.setText(
                " ТЕХНИЧЕСКОЕ ЗАДАНИЕ НА ЛР1 \n\n" +
                        "ТЕМА: Разработка игры \"Memory\" на Android\n\n" +
                        "ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ:\n" +
                        " 1. Отображать игровое поле минимального размера 4х4\n" +
                        " 2. В каждый момент времени на экране отображается не более одной картинки\n" +
                        " 3. Парное удаление карточек при совпадении\n" +
                        " 4. Возможность перезапуска игры\n\n" +
                        "ДОПОЛНИТЕЛЬНЫЕ ВОЗМОЖНОСТИ:\n" +
                        " Настройки игрового поля (реализовано)\n" +
                        " Запись информации (рекорды, время) (реализовано)\n" +
                        " Собственное оформление игры (реализовано)\n" +
                        " API сторонних сервисов (реализовано)\n\n" +
                        "ТРЕБОВАНИЯ К АВТОРСТВУ:\n" +
                        " 1. Не менее 3 признаков авторства (реализовано)\n" +
                        " 2. Текст с полной формулировкой задачи (реализовано)\n" +
                        " 3. Собственная иконка приложения (реализовано)\n\n" +
                        "ВЫПОЛНИЛ:\n" +
                        "Студент: Кацевич А. Ю.\n" +
                        "Группа: АС-66\n" +
                        "Дата выполнения: " + java.time.LocalDate.now() + "\n" +
                        "Оценка: _________\n" +
                        "Подпись преподавателя: __________"
        );

        backButton.setOnClickListener(v -> finish());
    }
}