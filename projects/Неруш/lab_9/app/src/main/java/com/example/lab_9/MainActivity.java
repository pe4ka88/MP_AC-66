package com.example.lab_9;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<>();
    BoxAdapter boxAdapter;
    TextView tvCartCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCartCount = findViewById(R.id.textView2);
        
        fillData();
        boxAdapter = new BoxAdapter(this, products);

        ListView lvMain = findViewById(R.id.lvSecond);
        lvMain.setAdapter(boxAdapter);
        
        updateCartCount();
    }

    void fillData() {
        products.add(new Product("White Sweater", 50, R.drawable.bel, false));
        products.add(new Product("Beige Jacket", 55, R.drawable.bez, false));
        products.add(new Product("Black Sweater", 30, R.drawable.kof, false));
        products.add(new Product("Black Golf", 60, R.drawable.koff, false));
        products.add(new Product("Black Jacket", 20, R.drawable.pid, false));
        products.add(new Product("Knitted Vest", 35, R.drawable.zil, false));
        products.add(new Product("Black Blazer", 40, R.drawable.pidch, false));
    }

    public void updateCartCount() {
        int count = 0;
        for (Product p : products) {
            if (p.box) count++;
        }
        tvCartCount.setText("Items in cart: " + count);
    }

    public void clickOpenFav(View view) {
        ArrayList<Product> favProducts = new ArrayList<>();
        for (Product p : products) {
            if (p.box) {
                favProducts.add(p);
            }
        }
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("list", favProducts);
        startActivityForResult(intent, 1);
    }

    public void clickOpenOrders(View view) {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Update products state based on basket changes
            ArrayList<Product> returnedList = (ArrayList<Product>) data.getSerializableExtra("list");
            for (Product p : products) {
                p.box = false; // Reset first
                for (Product rp : returnedList) {
                    if (p.name.equals(rp.name)) {
                        p.box = true;
                        break;
                    }
                }
            }
            boxAdapter.notifyDataSetChanged();
            updateCartCount();
        }
    }
}
