package com.example.lab_9;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        ListView listView = findViewById(R.id.listViewOrders);
        
        SharedPreferences prefs = getSharedPreferences("Orders", MODE_PRIVATE);
        Set<String> orderSet = prefs.getStringSet("order_list", new HashSet<String>());
        
        List<String> orderList = new ArrayList<>(orderSet);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_list_item_1, orderList);
        
        listView.setAdapter(adapter);
    }
}
