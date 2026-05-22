package com.example.lab_14;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MediaHistoryDao {
    @Insert
    void insert(MediaHistory history);

    @Query("SELECT * FROM media_history ORDER BY timestamp DESC")
    List<MediaHistory> getAllHistory();
}