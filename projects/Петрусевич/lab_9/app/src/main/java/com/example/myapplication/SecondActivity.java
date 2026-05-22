/*
package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    BoxAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Фильтруем основной список, оставляя только те товары, которые в корзине
        ArrayList<Product> favProducts = new ArrayList<>();
        for (Product p : MainActivity.products) {
            if (p.box) {
                favProducts.add(p);
            }
        }

        boxAdapter = new BoxAdapter(this, favProducts);
        ListView lvMainlvSecond = (ListView) findViewById(R.id.listViewFav);
        lvMainlvSecond.setAdapter(boxAdapter);
    }
}
*/
