package com.example.lab4mp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<>();
    ProductAdapter adapter;
    TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        View header = getLayoutInflater().inflate(R.layout.header_layout, null);
        listView.addHeaderView(header);

        Button btnGorobec = header.findViewById(R.id.btnGorobec);
        btnGorobec.setOnClickListener(v -> showPopup());

        View footer = getLayoutInflater().inflate(R.layout.footer_layout, null);
        txtCount = footer.findViewById(R.id.txtCount);
        Button btnShow = footer.findViewById(R.id.btnShow);
        listView.addFooterView(footer);

        products.add(new Product(1, "Телефон", 500));
        products.add(new Product(2, "Ноутбук", 1500));
        products.add(new Product(3, "Наушники", 80));
        products.add(new Product(4, "Часы", 120));

        adapter = new ProductAdapter(this, products);
        listView.setAdapter(adapter);

        // Переход в корзину
        btnShow.setOnClickListener(v -> {
            ArrayList<Product> selected = new ArrayList<>();
            for (Product p : products)
                if (p.isChecked()) selected.add(p);

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("cart", selected);
            startActivity(intent);
        });
    }

    private void showPopup() {
        String message =
                "1. Разработать приложение MiniShop, состоящее из двух Activity.\n" +
                        "2. В первом Activity создать список ListView с Header и Footer.\n" +
                        "3. В Footer разместить TextView и кнопку Show Checked Items.\n" +
                        "4. Реализовать кастомный адаптер (BaseAdapter).\n" +
                        "5. В каждом пункте списка: id, название, стоимость, чекбокс.\n" +
                        "6. В Footer отображать количество выбранных товаров.\n" +
                        "7. Кнопка Show Checked Items — переход во второе Activity.\n" +
                        "8. Корзина — кастомный список выбранных товаров.\n" +
                        "9. Продемонстрировать работу приложения.";

        new AlertDialog.Builder(this)
                .setTitle("Задание MiniShop")
                .setMessage(message)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
