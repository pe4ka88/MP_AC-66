package com.example.labb4new4;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ArrayList<Product> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart = (ArrayList<Product>) getIntent().getSerializableExtra("cart");

        ListView listView = findViewById(R.id.cartList);
        listView.setAdapter(new CartAdapter(this, cart));
    }
}
