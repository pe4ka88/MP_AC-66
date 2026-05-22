package com.example.lab4;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;
    private TextView tvTotalPrice;
    private Button btnBack;
    private CartAdapter adapter;
    private List<Product> selectedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Получаем данные из Intent
        selectedProducts = (List<Product>) getIntent().getSerializableExtra("selected_products");
        if (selectedProducts == null) {
            selectedProducts = new ArrayList<>();
        }

        listViewCart = findViewById(R.id.listViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnBack = findViewById(R.id.btnBack);

        // Создаем адаптер для корзины
        LayoutInflater inflater = LayoutInflater.from(this);
        adapter = new CartAdapter(selectedProducts, inflater);
        listViewCart.setAdapter(adapter);

        // Добавляем Header
        TextView header = new TextView(this);
        header.setText("🛒 Ваша корзина (Куган Н. Л.)");
        header.setPadding(16, 16, 16, 16);
        header.setTextSize(18);
        header.setTextColor(getResources().getColor(android.R.color.white));
        header.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        listViewCart.addHeaderView(header);

        // Подсчитываем общую сумму
        double total = 0;
        for (Product p : selectedProducts) {
            total += p.getPrice();
        }
        tvTotalPrice.setText(String.format("Итого: %.2f руб", total));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Возвращаемся назад
            }
        });
    }
}
