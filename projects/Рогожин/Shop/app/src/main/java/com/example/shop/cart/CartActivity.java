package com.example.shop.cart;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shop.R;
import com.example.shop.product.Product;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ArrayList<Product> cartItems;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView listView = findViewById(R.id.listViewCart);

        // Получаем выбранные товары из MainActivity
        cartItems = (ArrayList<Product>) getIntent().getSerializableExtra("cart");

        adapter = new CartAdapter(this, cartItems);
        listView.setAdapter(adapter);
    }
}
