package com.example.labb7new7;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView helpText = findViewById(R.id.helpText);

        helpText.setText(
                "Приложение для работы с мультимедиа:\n\n" +

                        "1. Аудиоплеер:\n" +
                        "- Выбор аудиофайла\n" +
                        "- Воспроизведение, пауза, остановка\n" +
                        "- Перемотка и регулировка громкости\n" +
                        "- Отображение времени\n\n" +

                        "2. Видеоплеер:\n" +
                        "- Выбор видеофайла\n" +
                        "- Воспроизведение, пауза, остановка\n" +
                        "- Перемотка, полноэкранный режим\n" +
                        "- Скрытие панели управления\n\n" +

                        "3. Фото:\n" +
                        "- Съёмка фото с камеры\n" +
                        "- Просмотр фото\n" +
                        "- Масштабирование и перемещение изображения\n\n" +

                        "Сценарий использования:\n" +
                        "• Выберите нужный раздел в главном меню.\n" +
                        "• Воспользуйтесь кнопками управления.\n" +
                        "• Для фото — сделайте снимок и увеличивайте его жестами.\n\n" +

                        "Требования:\n" +
                        "- Использовать собственные мультимедиа файлы\n" +
                        "- Не использовать одинаковые файлы с другими студентами\n\n" +

                        "Приложение разработано в рамках лабораторной работы №17."
        );
    }
}
