/*
package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

class Product implements Serializable {
    String name;
    int price;
    int image;
    boolean box;

    Product(String _describe, int _price, int _image, boolean _box) {
        name = _describe;
        price = _price;
        image = _image;
        box = _box;
    }
}

public class MainActivity extends Activity {

    public static ArrayList<Product> products = new ArrayList<>();
    BoxAdapter boxAdapter;
    public TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView2);

        if (products.isEmpty()) {
            fillData();
        }
        
        boxAdapter = new BoxAdapter(this, products);
        ListView lvMain = (ListView) findViewById(R.id.lvSecond);
        lvMain.setAdapter(boxAdapter);
        
        updateCounter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (boxAdapter != null) {
            boxAdapter.notifyDataSetChanged();
        }
        updateCounter();
    }

    void fillData() {
        products.add(new Product("Свитер (белый)", 50, R.drawable.bel, false));
        products.add(new Product("Пиджак (бежевый)", 55, R.drawable.bez, false));
        products.add(new Product("Свитер (черный)", 30, R.drawable.kof, false));
        products.add(new Product("Гольф (черный)", 60, R.drawable.koff, false));
        products.add(new Product("Пиджак (черный)", 20, R.drawable.pid, false));
        products.add(new Product("Жилет вязаный", 35, R.drawable.zil, false));
        products.add(new Product("Пиджак (черный)", 40, R.drawable.pidch, false));
    }

    public void updateCounter() {
        int count = 0;
        for (Product p : products) {
            if (p.box) count++;
        }
        if (tv != null) {
            tv.setText(String.valueOf(count));
        }
    }

    public void clickOpenFav(View view) {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }
}
*/
