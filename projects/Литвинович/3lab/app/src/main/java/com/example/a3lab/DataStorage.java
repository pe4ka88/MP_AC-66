package com.example.a3lab.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.a3lab.models.ItemModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "DataStorage";
    private static final String KEY_ITEMS = "cached_items";
    private Gson gson;

    public DataStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveItems(List<ItemModel> items) {
        String json = gson.toJson(items);
        sharedPreferences.edit().putString(KEY_ITEMS, json).apply();
    }

    public List<ItemModel> loadItems() {
        String json = sharedPreferences.getString(KEY_ITEMS, null);
        if (json != null) {
            Type type = new TypeToken<List<ItemModel>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return null;
    }

    public void clearCache() {
        sharedPreferences.edit().remove(KEY_ITEMS).apply();
    }
}