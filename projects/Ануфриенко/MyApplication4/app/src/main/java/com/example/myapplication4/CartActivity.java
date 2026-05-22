package com.example.myapplication4;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getWindow().setStatusBarColor(Color.parseColor("#1565C0"));

        ArrayList<Item> cart =
                (ArrayList<Item>) getIntent().getSerializableExtra("cart");

        ListView listView = findViewById(R.id.listView);
        TextView tvTotal  = findViewById(R.id.tvTotal);

        if (cart == null || cart.isEmpty()) {
            tvTotal.setText("Cart is empty");
            return;
        }

        listView.setAdapter(new CartAdapter(this, cart));

        double total = 0;
        for (Item item : cart) total += item.getPrice();
        tvTotal.setText(String.format("Total: %.2f BYN", total));
    }
}
