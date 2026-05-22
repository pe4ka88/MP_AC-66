package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ArrayList<Product> selectedProducts;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            selectedProducts = getIntent().getSerializableExtra("list", ArrayList.class);
        } else {
            //noinspection unchecked
            selectedProducts = (ArrayList<Product>) getIntent().getSerializableExtra("list");
        }

        if (selectedProducts == null) {
            selectedProducts = new ArrayList<>();
        }

        ListView lvFav = findViewById(R.id.listViewFav);
        // 8. Корзину товаров реализовать в виде нового кастомизированного списка с выбранными товарами.
        BoxAdapter adapter = new BoxAdapter(this, selectedProducts, null);
        lvFav.setAdapter(adapter);
    }
}
