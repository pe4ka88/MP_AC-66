package com.example.lab7.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_entries")
public class HistoryEntry {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String module;

    @NonNull
    public String action;

    public long timestamp;

    public HistoryEntry(@NonNull String module, @NonNull String action, long timestamp) {
        this.module = module;
        this.action = action;
        this.timestamp = timestamp;
    }
}
