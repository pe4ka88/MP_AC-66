package com.example.laba1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvBoard;
    private TextView tvInfo;
    private Button btnRestart, btnTask;

    private CardsAdapter adapter;
    private final List<Card> cards = new ArrayList<>();

    private Integer openedPos = null;
    private int remainingPairs = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvBoard = findViewById(R.id.rvBoard);
        tvInfo = findViewById(R.id.tvInfo);
        btnRestart = findViewById(R.id.btnRestart);
        btnTask = findViewById(R.id.btnTask);

        rvBoard.setLayoutManager(new GridLayoutManager(this, 4));

        startNewGame();

        btnRestart.setOnClickListener(v -> startNewGame());
        btnTask.setOnClickListener(v -> showTaskDialog());
    }

    private void startNewGame() {
        cards.clear();
        openedPos = null;

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.img1);
        images.add(R.drawable.img2);
        images.add(R.drawable.img3);
        images.add(R.drawable.img4);
        images.add(R.drawable.img5);
        images.add(R.drawable.img6);
        images.add(R.drawable.img7);
        images.add(R.drawable.img8);

        List<Integer> pool = new ArrayList<>();
        pool.addAll(images);
        pool.addAll(images);

        Collections.shuffle(pool, new Random(System.currentTimeMillis()));

        for (int res : pool) {
            cards.add(new Card(res));
        }

        remainingPairs = images.size();
        updateInfo();

        adapter = new CardsAdapter(cards, this::onCardClick);
        rvBoard.setAdapter(adapter);
    }

    private void onCardClick(int pos) {
        Card current = cards.get(pos);
        if (current.isRemoved) return;

        if (openedPos != null && openedPos == pos) return;

        Integer prevPos = openedPos;

        if (prevPos == null) {
            openedPos = pos;
            adapter.openedPos = pos;
            adapter.refresh();
            return;
        }


        Card prev = cards.get(prevPos);
        boolean isMatch = (prev.imageRes == current.imageRes);

        if (isMatch) {
            prev.isRemoved = true;
            current.isRemoved = true;
            remainingPairs -= 1;
        }


        openedPos = (current.isRemoved) ? null : pos;
        adapter.openedPos = openedPos;

        updateInfo();
        adapter.refresh();

        if (remainingPairs == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Победа!")
                    .setMessage("Все пары удалены.\nРазработал: Занько Я.С., Группы АС-66")
                    .setPositiveButton("Ок", null)
                    .show();
        }
    }

    private void updateInfo() {
        tvInfo.setText("Осталось пар: " + remainingPairs);
    }

    private void showTaskDialog() {
        String text =
                "Лабораторная работа №1. Игра “Память” на Android.\n\n" +
                "Требования:\n" +
                "1) Поле минимум 4×4.\n" +
                "2) В каждый момент времени на экране не более одной картинки.\n" +
                "3) При открытии следующей картинки происходит парное удаление только при совпадении.\n" +
                "4) Возможность перезапустить игру.\n\n" +
                "Выполнил: Занько Я.С. (Группа АС-66)";

        new AlertDialog.Builder(this)
                .setTitle("Задание ЛР1")
                .setMessage(text)
                .setPositiveButton("Закрыть", null)
                .show();
    }
}
