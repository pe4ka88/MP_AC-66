package com.example.myapplication4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Item> items;
    private TextView   tvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.parseColor("#1565C0"));

        ListView listView = findViewById(R.id.listView);

        // --- Header ---
        View header = LayoutInflater.from(this)
                .inflate(R.layout.list_header, listView, false);
        listView.addHeaderView(header, null, false);

        // --- Footer ---
        View footer = LayoutInflater.from(this)
                .inflate(R.layout.list_footer, listView, false);
        tvCount = footer.findViewById(R.id.tvCount);
        Button btnCart = footer.findViewById(R.id.btnCart);
        listView.addFooterView(footer, null, false);

        // --- Данные ---
        items = new ArrayList<>(Arrays.asList(
                new Item(1, "Наушники",   890.00),
                new Item(2, "USB-C Hub",    42.29),
                new Item(3, "Клавиатура",     289.99),
                new Item(4, "Webcam",       39.99),
                new Item(5, "Коврик для мыши",     39.89),
                new Item(6, "Лампа настольная",    124.10),
                new Item(7, "Подставка для ноутбука", 34.99),
                new Item(8, "Ноутбук",       5895.00),
                new Item(9, "Смартфон",     780.95),
                new Item(10, "Планшет",    2499.99),
                new Item(11, "Монитор", 654.30),
                new Item(12, "TWS-наушники",      99.80),
                new Item(11, "Монитор", 654.30),
                new Item(12, "TWS-наушники",      99.80)
        ));

        // --- Адаптер ---
        listView.setAdapter(new ProductAdapter(this, items, this::updateCount));
        updateCount();

        // --- Переход в корзину ---
        btnCart.setOnClickListener(v -> {
            ArrayList<Item> selected = new ArrayList<>();
            for (Item item : items)
                if (item.isChecked()) selected.add(item);

            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("cart", selected);
            startActivity(intent);
        });
    }

    private void updateCount() {
        int count = 0;
        for (Item item : items)
            if (item.isChecked()) count++;
        tvCount.setText("Selected: " + count);
    }
}