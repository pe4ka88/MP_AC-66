package com.example.lr4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lr4.adapters.CartAdapter;
import com.example.lr4.model.Product;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED = "extra_selected_products";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Button btnBack = findViewById(R.id.btnBack);
        ListView listViewCart = findViewById(R.id.listViewCart);

        ArrayList<Product> selected;
        try {
            selected = (ArrayList<Product>) getIntent().getSerializableExtra(EXTRA_SELECTED);
        } catch (Exception e) {
            selected = new ArrayList<>();
        }
        if (selected == null) selected = new ArrayList<>();

        listViewCart.setAdapter(new CartAdapter(this, selected));

        if (selected.isEmpty()) {
            Toast.makeText(this, "Корзина пуста (Занько Я.С., АС-66)", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }
}
