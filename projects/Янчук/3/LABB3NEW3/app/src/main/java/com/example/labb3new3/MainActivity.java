package com.example.labb3new3;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labb3new3.API.ApiService;
import com.example.labb3new3.Retrofit.RetrofitClient;
import com.example.labb3new3.model.Item;
import com.example.labb3new3.ui.ListFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   // ← ДОЛЖНО БЫТЬ ПЕРВЫМ

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Вставляем фрагмент ТОЛЬКО после setContentView
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new ListFragment())
                    .commit();
        }

        // Тестовый запрос (можно удалить)
        ApiService api = RetrofitClient.getClient().create(ApiService.class);

        api.getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    List<Item> items = response.body();
                    Log.d("SERVER", "Получено элементов: " + items.size());
                } else {
                    Log.e("SERVER", "Ошибка ответа: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.e("SERVER", "Ошибка запроса: " + t.getMessage());
            }
        });
    }
}
