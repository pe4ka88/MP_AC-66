package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new ListFragment())
                    .commit();
        }
    }

    public static class Item {
        String title, description, fullInfo;
        Item(String t, String d, String f) { this.title = t; this.description = d; this.fullInfo = f; }
    }

    public static class ListFragment extends Fragment {
        List<Item> items = new ArrayList<>();
        ItemAdapter adapter;
        EditText urlInput;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_list, container, false);

            urlInput = v.findViewById(R.id.urlInput);
            Button btnLoad = v.findViewById(R.id.btnLoad);
            Button btnSaveCsv = v.findViewById(R.id.btnSave);
            RecyclerView rv = v.findViewById(R.id.recyclerView);

            adapter = new ItemAdapter(items, item -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_container, new DetailFragment(item))
                        .addToBackStack(null).commit();
            });

            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(adapter);

            btnLoad.setOnClickListener(view -> {
                String url = urlInput.getText().toString();
                if (!url.isEmpty()) loadDataFromUrl(url);
            });

            btnSaveCsv.setOnClickListener(view -> {
                if (items.isEmpty()) {
                    Toast.makeText(getContext(), "Список пуст", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveAllToCsv();
            });

            return v;
        }

        private void saveAllToCsv() {
            StringBuilder csv = new StringBuilder("Title,Description\n");
            for (Item i : items) csv.append(i.title).append(",").append(i.description).append("\n");
            try (FileOutputStream fos = getContext().openFileOutput("data.csv", Context.MODE_PRIVATE)) {
                fos.write(csv.toString().getBytes());
                Toast.makeText(getContext(), "Сохранено в data.csv", Toast.LENGTH_SHORT).show();
            } catch (Exception e) { e.printStackTrace(); }
        }

        private void loadDataFromUrl(String url) {
            new Thread(() -> {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String jsonString = response.body().string();
                        java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<Item>>(){}.getType();
                        List<Item> newItems = new Gson().fromJson(jsonString, listType);

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                items.clear();
                                items.addAll(newItems);
                                adapter.notifyDataSetChanged();
                            });
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }).start();
        }
    }

    public static class DetailFragment extends Fragment {
        Item item;
        public DetailFragment(Item item) { this.item = item; }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(LinearLayout.VERTICAL);
            l.setPadding(60, 200, 60, 60);

            l.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            l.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv = new TextView(getContext());
            tv.setText(item.fullInfo);
            tv.setTextSize(20);
            tv.setTextColor(android.graphics.Color.BLACK);
            tv.setPadding(0, 0, 0, 80);
            tv.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnParams.gravity = Gravity.CENTER_HORIZONTAL;
            btnParams.setMargins(0, 20, 0, 20);

            Button btnShare = new Button(getContext());
            btnShare.setText("Сохранить и отправить");
            btnShare.setLayoutParams(btnParams);
            btnShare.setOnClickListener(v -> {
                String folderName = "reports";
                String fileName = "data.txt";
                java.io.File directory = new java.io.File(getContext().getFilesDir(), folderName);
                if (!directory.exists()) directory.mkdirs();
                java.io.File file = new java.io.File(directory, fileName);

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(item.fullInfo.getBytes());
                    Toast.makeText(getContext(), "Сохранено в " + folderName, Toast.LENGTH_SHORT).show();
                } catch (Exception e) { e.printStackTrace(); }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, item.fullInfo);
                startActivity(Intent.createChooser(intent, "Поделиться через..."));
            });

            Button btnBack = new Button(getContext());
            btnBack.setText("Назад");
            btnBack.setLayoutParams(btnParams);
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

            l.addView(tv);
            l.addView(btnShare);
            l.addView(btnBack);
            return l;
        }
    }

    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {
        List<Item> data;
        OnItemClickListener listener;
        public interface OnItemClickListener { void onClick(Item item); }
        ItemAdapter(List<Item> data, OnItemClickListener l) { this.data = data; this.listener = l; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Item item = data.get(position);
            holder.t1.setText(item.title);
            holder.t2.setText(item.description);
            holder.itemView.setOnClickListener(v -> listener.onClick(item));
        }

        @Override
        public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(View v) {
                super(v);
                t1 = v.findViewById(R.id.textTitle);
                t2 = v.findViewById(R.id.textDesc);
            }
        }
    }
}