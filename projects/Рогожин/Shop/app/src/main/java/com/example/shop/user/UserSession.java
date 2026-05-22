package com.example.shop.user;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {

    private static final String PREF = "session";
    private static final String KEY = "user";

    public static void setUser(Context context, String email) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        prefs.edit().putString(KEY, email).apply();
    }

    public static String getUser(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        return prefs.getString(KEY, null);
    }

    public static void logout(Context context) {

        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        prefs.edit().clear().apply();
    }
}
