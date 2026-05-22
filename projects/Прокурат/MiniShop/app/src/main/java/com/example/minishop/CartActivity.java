package com.example.minishop;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minishop.adapters.ProductAdapter;
import com.example.minishop.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;
    private Button buttonBack;
    private ProductAdapter adapter;
    private List<Product> cartProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listViewCart = findViewById(R.id.listViewCart);
        buttonBack = findViewById(R.id.buttonBack);

        cartProducts = (List<Product>) getIntent().getSerializableExtra("selected_products");
        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }

        adapter = new ProductAdapter(this, cartProducts, null) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                CheckBox checkBox = view.findViewById(R.id.checkBoxProduct);
                checkBox.setEnabled(false);
                checkBox.setChecked(true);
                return view;
            }
        };

        listViewCart.setAdapter(adapter);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        double total = 0;
        for (Product p : cartProducts) {
            total += p.getPrice();
        }

        addTotalFooter(total);
    }

    private void addTotalFooter(double total) {
        TextView footerView = new TextView(this);
        footerView.setText(String.format("\n\nИтого: %.2f руб", total));
        footerView.setTextColor(getResources().getColor(R.color.pink_dark));
        footerView.setTextSize(18);
        footerView.setPadding(16, 16, 16, 16);
        footerView.setGravity(android.view.Gravity.CENTER);
        footerView.setBackgroundColor(getResources().getColor(R.color.purple_light));

        listViewCart.addFooterView(footerView);
    }
}