package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends Activity {

    ArrayList<Product> products = new ArrayList<>();
    BoxAdapter boxAdapter;
    TextView tvCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillData();

        ListView lvMain = findViewById(R.id.lvSecond);

        // 2. В первом Activity создать список ListView с Header и Footer.
        View header = getLayoutInflater().inflate(R.layout.header, null);
        lvMain.addHeaderView(header);

        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        lvMain.addFooterView(footer);

        // 3. В Footer разместить текстовое поле (TextView) для ввода количества...
        tvCount = footer.findViewById(R.id.tvCount);
        // 3. ...кнопку Show Checked Items для перехода в корзину товаров.
        footer.findViewById(R.id.btnShowChecked).setOnClickListener(v -> {
            // 7. При нажатии кнопки Show Checked Items реализовать переход во второе Activity с корзиной товаров.
            ArrayList<Product> selectedProducts = boxAdapter.getBox();
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra("list", selectedProducts);
            startActivity(intent);
        });

        // 6. В текстовом поле (TextView) Footer списка динамически отображать общее текущее количество активированных товаров.
        boxAdapter = new BoxAdapter(this, products, count -> {
            tvCount.setText("Выбрано товаров: " + count);
        });

        lvMain.setAdapter(boxAdapter);
    }

    void fillData() {
        products.add(new Product(1, "Смартфон", "50000", false));
        products.add(new Product(2, "Ноутбук", "80000", false));
        products.add(new Product(3, "Наушники", "5000", false));
        products.add(new Product(4, "Клавиатура", "3000", false));
        products.add(new Product(5, "Мышь", "1500", false));
    }
}
