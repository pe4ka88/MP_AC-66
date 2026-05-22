package com.example.lab4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView lvCart = findViewById(R.id.lvCart);
        Button btnCartBack = findViewById(R.id.btnCartBack);
        TextView tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnCartBack.setOnClickListener(v -> finish());

        ArrayList<Product> checkedItems = (ArrayList<Product>) getIntent().getSerializableExtra("checkedItems");

        if (checkedItems != null) {
            CartAdapter adapter = new CartAdapter(this, checkedItems);
            lvCart.setAdapter(adapter);

            double total = 0;
            for (Product p : checkedItems) {
                total += p.getPrice();
            }
            tvTotalPrice.setText("Общая стоимость: " + String.format("%.2f", total));
        }
    }
}
