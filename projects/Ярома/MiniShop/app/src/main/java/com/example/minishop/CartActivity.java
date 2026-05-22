package com.example.minishop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;
    private TextView tvCartInfo;
    private ArrayList<Good> checkedGoods;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listViewCart = findViewById(R.id.listViewCart);
        tvCartInfo = findViewById(R.id.tvCartInfo);

        checkedGoods = getIntent().getParcelableArrayListExtra("checked_goods");

        if (checkedGoods == null) {
            checkedGoods = new ArrayList<>();
        }

        cartAdapter = new CartAdapter(this, checkedGoods);
        listViewCart.setAdapter(cartAdapter);

        tvCartInfo.setText("В корзине товаров: " + checkedGoods.size());
    }
}