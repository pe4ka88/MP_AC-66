package com.example.thirdlab9;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Activity корзины товаров — отображает только выбранные товары
 *
 * @author Пекун Марк Сергеевич, группа АС-66
 * Лабораторная работа №9. Механизмы обратного вызова
 */
public class SecondActivity extends AppCompatActivity {

    private ListView           listViewCart;
    private TextView           tvCartSubtitle;
    private TextView           tvTotalItems;
    private TextView           tvTotalPrice;
    private ImageButton        btnBack;
    private ArrayList<Product> checkedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        listViewCart   = findViewById(R.id.listViewCart);
        tvCartSubtitle = findViewById(R.id.tvCartSubtitle);
        tvTotalItems   = findViewById(R.id.tvTotalItems);
        tvTotalPrice   = findViewById(R.id.tvTotalPrice);
        btnBack        = findViewById(R.id.btnBack);

        // Получение выбранных товаров из Intent
        Intent intent = getIntent();
        checkedProducts = intent.getParcelableArrayListExtra("checkedProducts");

        if (checkedProducts != null && !checkedProducts.isEmpty()) {
            tvCartSubtitle.setText(String.format(Locale.getDefault(),
                    "%d товар(ов) — Автор: Пекун М.С., АС-66", checkedProducts.size()));
            tvTotalItems.setText(String.format(Locale.getDefault(),
                    "%d шт.", checkedProducts.size()));

            double total = 0;
            for (Product p : checkedProducts) total += p.getPrice();
            tvTotalPrice.setText(String.format(Locale.getDefault(), "%.2f BYN", total));

            CartAdapter adapter = new CartAdapter(this, checkedProducts);
            listViewCart.setAdapter(adapter);
        } else {
            tvCartSubtitle.setText("Корзина пуста — Автор: Пекун М.С., АС-66");
            tvTotalItems.setText("0 шт.");
            tvTotalPrice.setText("0.00 BYN");
        }

        // Кнопка назад
        btnBack.setOnClickListener(v -> onBackPressed());

        // Кнопка Checkout
        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (checkedProducts == null || checkedProducts.isEmpty()) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Заказ оформлен! " + checkedProducts.size() + " товар(ов)\nПекун М.С., АС-66",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
