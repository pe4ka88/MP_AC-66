package com.example.shopezepchukac66;

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
