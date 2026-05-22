package com.example.lab7.data;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HistoryLogger {

    private static final ExecutorService DB_EXECUTOR = Executors.newSingleThreadExecutor();

    private HistoryLogger() {
    }

    public static void log(Context context, String module, String action) {
        DB_EXECUTOR.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.historyDao().insert(new HistoryEntry(module, action, System.currentTimeMillis()));
        });
    }
}
