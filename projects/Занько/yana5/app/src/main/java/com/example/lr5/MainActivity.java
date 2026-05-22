package com.example.lr5;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private NotesPagerAdapter pagerAdapter;
    private NotesDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new NotesDbHelper(this);

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new NotesPagerAdapter(getSupportFragmentManager(), dbHelper);
        viewPager.setAdapter(pagerAdapter);

        Button btnTask = findViewById(R.id.btnTask);
        btnTask.setOnClickListener(v -> showTaskDialog());
    }

    public void refreshShowFragment() {
        if (pagerAdapter != null) {
            pagerAdapter.refreshShowFragment();
        }
    }

    public void openShowTab() {
        if (viewPager != null) {
            viewPager.setCurrentItem(0, true);
        }
    }

    private void showTaskDialog() {
        String taskText =
                "Лабораторная работа №10\\n\\n" +
                "Тема: Фрагменты. ViewPager. Хранение информации в базе данных SQLite\\n\\n" +
                "Практическое задание:\\n" +
                "1. Разработать приложение MyNotes, представляющее собой View Pager.\\n" +
                "2. Поместить в View Pager четыре фрагмента: FragmentShow, FragmentAdd, FragmentDel, FragmentUpdate.\\n" +
                "3. В View Pager добавить верхнее меню вкладок PagerTabStrip с заголовками Show, Add, Del, Update.\\n" +
                "4. Во фрагменте FragmentShow реализовать кастомизированный список заметок ListView с помощью собственного адаптера.\\n" +
                "5. В каждом пункте списка отобразить номер и описание заметки.\\n" +
                "6. Хранение заметок реализовать через SQLite.\\n" +
                "7. Во FragmentAdd добавить заметку по кнопке Add.\\n" +
                "8. Во FragmentDel удалить заметку по номеру по кнопке Del.\\n" +
                "9. Во FragmentUpdate обновить заметку по номеру по кнопке Update.\\n" +
                "10. В БД должно быть не менее 20 записей.\\n\\n" +
                "Выполнил: Занько Я.С.\\n" +
                "Группа: АС-66";

        new AlertDialog.Builder(this)
                .setTitle("Задание лабораторной работы")
                .setMessage(taskText)
                .setPositiveButton("OK", null)
                .show();
    }
}
