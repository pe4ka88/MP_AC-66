package com.example.a3lab.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {
    private static final String PREF_NAME = "app_settings";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_ENDPOINT = "endpoint";

    private final SharedPreferences prefs;

    public SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getServerUrl() {
        return prefs.getString(KEY_SERVER_URL, "https://jsonplaceholder.typicode.com");
    }

    public String getSelectedEndpoint() {
        return prefs.getString(KEY_ENDPOINT, "/posts");
    }

    public void saveSettings(String serverUrl, String endpoint) {
        prefs.edit()
                .putString(KEY_SERVER_URL, serverUrl)
                .putString(KEY_ENDPOINT, endpoint)
                .apply();
    }
}