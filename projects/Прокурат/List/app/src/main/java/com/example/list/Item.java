package com.example.list;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Item implements Serializable {
    private int id;
    private int userId;
    private String title;

    @SerializedName("content")
    private String body;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}