package com.example.lab4mp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ArrayList<Product> cart;
    CartAdapter adapter;
    TextView txtTotal;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView listView = findViewById(R.id.cartListView);
        txtTotal = findViewById(R.id.txtTotal);
        btnDelete = findViewById(R.id.btnDeleteSelected);

        cart = (ArrayList<Product>) getIntent().getSerializableExtra("cart");

        adapter = new CartAdapter(this, cart);
        listView.setAdapter(adapter);

        updateTotal();
        updateButton();

        btnDelete.setOnClickListener(v -> {

            // Если корзина пустая → просто назад
            if (cart.isEmpty()) {
                finish();
                return;
            }

            ArrayList<Product> toRemove = new ArrayList<>();

            for (Product p : cart) {
                if (p.isChecked()) {
                    toRemove.add(p);
                }
            }

            cart.removeAll(toRemove);
            adapter.notifyDataSetChanged();
            updateTotal();
            updateButton();

            // Если после удаления корзина стала пустой → назад
            if (cart.isEmpty()) {
                finish();
            }
        });
    }

    public void updateTotal() {
        int sum = 0;
        for (Product p : cart) {
            sum += p.getPrice();
        }
        txtTotal.setText("Итого: " + sum);
    }

    private void updateButton() {
        if (cart.isEmpty()) {
            btnDelete.setText("Назад в каталог");
        } else {
            btnDelete.setText("Удалить выбранные");
        }
    }
}
