package com.example.lab5mp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Создаём БД
        DBHelper db = new DBHelper(this);

        // Настраиваем ViewPager
        ViewPager viewPager = findViewById(R.id.viewPager);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        Button btnInfo = findViewById(R.id.btnInfo);

        btnInfo.setOnClickListener(v -> {
            String text =
                    "Одним из доступных способов заранее подготовьте базу данных. База данных, содержащая менее 20 записей будет считаться отсутствующей.\n" +
                            "1. Разработать приложение MyNotes, представляющее собой View Pager.\n" +
                            "2. Поместить в View Pager четыре фрагмента: FragmentShow, FragmentAdd, FragmentDel, FragmentUpdate.\n" +
                            "3. В View Pager добавить верхнее меню вкладок (PagerTabStrip) с заголовками Show, Add, Del, Update.\n" +
                            "4. Во фрагменте FragmentShow реализовать кастомизированный список заметок ListView с помощью собственного адаптера.\n" +
                            "5. В каждом пункте списка отобразить следующую информацию о заметке пользователя: номер, описание заметки.\n" +
                            "6. Хранение, а также предоставление информации о заметках адаптеру реализовать с помощью базы данных SQLite.\n" +
                            "7. Во фрагменте FragmentAdd реализовать функционал добавления новой заметки посредством ввода описания заметки в поле EditText и добавления информации в базу данных SQLite по нажатию кнопки Add.\n" +
                            "8. Во фрагменте FragmentDel реализовать функционал удаления новой заметки посредством ввода ее номера в поле EditText и удаления информации из базы данных SQLite по нажатию кнопки Del.\n" +
                            "9. Во фрагменте FragmentUpdate реализовать функционал обновления существующей заметки посредством ввода ее номера в поле EditText, ввода нового описания в поле EditText и обновления информации из базы данных SQLite по нажатию кнопки Update.\n" +
                            "10. База данных, содержащая менее 20 записей будет считаться отсутствующей.";

            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Задание лабораторной")
                    .setMessage(text)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}
