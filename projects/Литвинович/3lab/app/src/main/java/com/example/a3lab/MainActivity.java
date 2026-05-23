package com.example.a3lab;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.a3lab.fragments.DetailFragment;
import com.example.a3lab.fragments.ListFragment;
import com.example.a3lab.models.ItemModel;
import com.example.a3lab.network.ApiService;
import com.example.a3lab.utils.DataStorage;

public class MainActivity extends AppCompatActivity implements ListFragment.OnItemSelectedListener {
    private ListFragment listFragment;
    private DetailFragment detailFragment;
    private Button buttonLoadData;
    private Button buttonSettings;
    private Button buttonClearCache;
    private LinearLayout linearLayoutButtons;
    private ApiService apiService;
    private DataStorage dataStorage;
    private boolean isTabletMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLoadData = findViewById(R.id.buttonLoadData);
        buttonSettings = findViewById(R.id.buttonSettings);
        buttonClearCache = findViewById(R.id.buttonClearCache);
        linearLayoutButtons = findViewById(R.id.linearLayoutButtons);

        apiService = new ApiService(this);
        dataStorage = new DataStorage(this);

        listFragment = new ListFragment();
        listFragment.setOnItemSelectedListener(this);

        detailFragment = new DetailFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutList, listFragment)
                .commit();

        if (findViewById(R.id.frameLayoutDetail) != null) {
            isTabletMode = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayoutDetail, detailFragment)
                    .commit();
        }

        buttonLoadData.setOnClickListener(v -> listFragment.loadDataFromServer());

        buttonSettings.setOnClickListener(v -> showSettingsDialog());

        buttonClearCache.setOnClickListener(v -> {
            dataStorage.clearCache();
            Toast.makeText(MainActivity.this, "Кэш очищен", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Настройки подключения");

        View view = getLayoutInflater().inflate(R.layout.dialog_settings, null);
        EditText editTextServerUrl = view.findViewById(R.id.editTextServerUrl);
        EditText editTextLimit = view.findViewById(R.id.editTextLimit);

        String currentUrl = apiService.getServerUrl();
        editTextServerUrl.setText(currentUrl);

        builder.setView(view);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newUrl = editTextServerUrl.getText().toString().trim();
            String limit = editTextLimit.getText().toString().trim();

            if (!newUrl.isEmpty()) {
                if (!limit.isEmpty()) {
                    newUrl = "https://jsonplaceholder.typicode.com/posts?_limit=" + limit;
                }
                apiService.setServerUrl(newUrl);
                Toast.makeText(MainActivity.this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    @Override
    public void onItemSelected(ItemModel item) {
        if (isTabletMode) {
            detailFragment.displayItem(item);
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("item", item);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutList, detailFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            linearLayoutButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isTabletMode && getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            linearLayoutButtons.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}