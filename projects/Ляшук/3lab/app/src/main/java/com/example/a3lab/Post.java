package com.example.a3lab;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Post implements Serializable {
    private int id;
    
    @SerializedName(value = "title", alternate = "name")
    private String title;
    
    @SerializedName(value = "body", alternate = "status")
    private String body;
    
    @SerializedName(value = "thumbnailUrl", alternate = "image")
    private String thumbnailUrl;
    
    @SerializedName("url")
    private String url;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getUrl() { 
        return url != null ? url : thumbnailUrl; 
    }
}