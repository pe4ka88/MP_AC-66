package com.example.thirdlab9;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity "О задании" — полное описание лабораторной работы и авторство
 *
 * @author Пекун Марк Сергеевич, группа АС-66
 * Лабораторная работа №9
 */
public class ThirdActivity extends AppCompatActivity {

    private static final String[] TASKS = {
        "Разработать приложение MiniShop из двух Activity",
        "Создать ListView с Header и Footer",
        "В Footer: TextView с количеством товаров и кнопка Show Checked Items",
        "Реализовать кастомный адаптер, наследующий BaseAdapter",
        "Отображать ID, название, цену и CheckBox в каждом элементе",
        "Реализовать корзину выбранных товаров во втором Activity"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // Кнопка назад
        ImageButton btnBack = findViewById(R.id.btnBackThird);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Заполнение строк задач
        int[] taskIds = {
            R.id.task1, R.id.task2, R.id.task3,
            R.id.task4, R.id.task5, R.id.task6
        };
        for (int i = 0; i < taskIds.length; i++) {
            TextView tv = findViewById(taskIds[i]).findViewById(R.id.tvTaskText);
            tv.setText(TASKS[i]);
        }

        // Кнопка подтверждения авторства
        findViewById(R.id.btnAuthorProof).setOnClickListener(v ->
            Toast.makeText(this,
                "✓ Работу выполнил Пекун Марк Сергеевич, группа АС-66",
                Toast.LENGTH_LONG).show()
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
