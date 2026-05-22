package com.example.lab_9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SecondActivity extends AppCompatActivity {
    BoxAdapter boxAdapter;
    ArrayList<Product> basketList;
    TextView tvTotal;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        basketList = (ArrayList<Product>) getIntent().getSerializableExtra("list");
        if (basketList == null) basketList = new ArrayList<>();

        tvTotal = findViewById(R.id.tvTotal);
        boxAdapter = new BoxAdapter(this, basketList);

        ListView lvMain = findViewById(R.id.listViewFav);
        lvMain.setAdapter(boxAdapter);

        updateTotal();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("list", basketList);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    public void updateTotal() {
        int total = 0;
        for (Product p : basketList) {
            total += p.price;
        }
        tvTotal.setText(total + " $");
    }

    public void clickOrder(View view) {
        if (basketList.isEmpty()) {
            Toast.makeText(this, "Basket is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save order to SharedPreferences
        saveOrder();

        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();
        
        // Clear basket and return
        basketList.clear();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("list", basketList);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void saveOrder() {
        SharedPreferences prefs = getSharedPreferences("Orders", MODE_PRIVATE);
        Set<String> orders = prefs.getStringSet("order_list", new HashSet<String>());
        Set<String> newOrders = new HashSet<>(orders != null ? orders : new HashSet<String>());
        
        StringBuilder orderDetails = new StringBuilder();
        int total = 0;
        for (Product p : basketList) {
            orderDetails.append(p.name).append(", ");
            total += p.price;
        }
        String orderInfo = "Order #" + (newOrders.size() + 1) + ": " + orderDetails.toString() + " Total: " + total + " $";
        newOrders.add(orderInfo);
        
        prefs.edit().putStringSet("order_list", newOrders).apply();
    }
}
