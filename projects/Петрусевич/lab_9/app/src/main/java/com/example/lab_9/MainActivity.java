package com.example.lab_9;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Product> products = new ArrayList<>();
    BoxAdapter boxAdapter;
    TextView tvCartCount;

    private final ActivityResultLauncher<Intent> secondActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<Product> returnedList;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        returnedList = result.getData().getSerializableExtra("list", ArrayList.class);
                    } else {
                        //noinspection unchecked
                        returnedList = (ArrayList<Product>) result.getData().getSerializableExtra("list");
                    }

                    if (returnedList != null) {
                        for (Product p : products) {
                            p.box = false; // Reset first
                            for (Object obj : returnedList) {
                                if (obj instanceof Product) {
                                    Product rp = (Product) obj;
                                    if (p.name.equals(rp.name)) {
                                        p.box = true;
                                        break;
                                    }
                                }
                            }
                        }
                        boxAdapter.notifyDataSetChanged();
                        updateCartCount();
                    }
                }
            }
    );

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
        products.add(new Product("Black suitcase", 50, R.drawable.bel, false));
        products.add(new Product("Grey suitcase", 45, R.drawable.bez, false));
        products.add(new Product("White suitcase", 45, R.drawable.kof, false));
        products.add(new Product("Pink suitcase", 42, R.drawable.koff, false));
        products.add(new Product("Red suitcase", 40, R.drawable.pid, false));
        products.add(new Product("Double suitcase", 60, R.drawable.zil, false));
        products.add(new Product("Black sequin suitcase", 50, R.drawable.pidch, false));
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
        secondActivityLauncher.launch(intent);
    }

    public void clickOpenOrders(View view) {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }
}
