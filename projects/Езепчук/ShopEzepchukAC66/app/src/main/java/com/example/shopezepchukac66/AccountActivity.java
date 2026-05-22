package com.example.shopezepchukac66;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    ListView listCart;
    ArrayList<Product> cartProducts;
    CartAdapter adapter;

    // Footer
    LinearLayout storeSection, accountSection, historySection;
    TextView txtCount;
    ImageButton btnAccount;
    Button btnOrder;

    OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        orderRepository = new OrderRepository(this);

        listCart = findViewById(R.id.listCart);
        btnOrder = findViewById(R.id.btnOrder);

        cartProducts = new ArrayList<>(CartManager.getCart());

        adapter = new CartAdapter(this, cartProducts);
        listCart.setAdapter(adapter);

        btnAccount = findViewById(R.id.btnAccount);

        btnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, ProfActivity.class);
            startActivity(intent);
        });

        LinearLayout footer = findViewById(R.id.footerLayout);
        txtCount = footer.findViewById(R.id.txtCount);
        storeSection = footer.findViewById(R.id.sectionStore);
        accountSection = footer.findViewById(R.id.sectionAccount);
        historySection = footer.findViewById(R.id.sectionHistory);

        updateCounter();

        storeSection.setOnClickListener(v -> {
            startActivity(new Intent(AccountActivity.this, MainActivity.class));
            finish();
        });

        accountSection.setOnClickListener(v ->
                Toast.makeText(this, "Аккаунт", Toast.LENGTH_SHORT).show()
        );

        historySection.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class))
        );

        btnOrder.setOnClickListener(v -> makeOrder());
    }

    private void makeOrder() {

        if (cartProducts.isEmpty()) {
            Toast.makeText(this, "Корзина пуста!", Toast.LENGTH_SHORT).show();
            return;
        }

        String user = UserSession.getUser(this);

        if (user == null || user.isEmpty()) {
            Toast.makeText(this, "Ошибка пользователя", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Product product : cartProducts) {

            orderRepository.saveOrder(
                    product.getTitle(),
                    product.getPrice()
            );
        }

        CartManager.clearCart();

        Toast.makeText(this, "Заказ оформлен!", Toast.LENGTH_SHORT).show();

        cartProducts.clear();
        adapter.notifyDataSetChanged();
        updateCounter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        cartProducts.clear();
        cartProducts.addAll(CartManager.getCart());

        adapter.notifyDataSetChanged();
        updateCounter();
    }

    private void updateCounter() {
        txtCount.setText("В корзине: " + cartProducts.size());
    }
}