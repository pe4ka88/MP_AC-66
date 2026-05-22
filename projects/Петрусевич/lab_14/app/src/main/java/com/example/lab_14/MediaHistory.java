package com.example.lab_14;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_history")
public class MediaHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String mediaType; // Audio, Video, Photo
    public String mediaName;
    public long timestamp;

    public MediaHistory(String mediaType, String mediaName, long timestamp) {
        this.mediaType = mediaType;
        this.mediaName = mediaName;
        this.timestamp = timestamp;
    }
}