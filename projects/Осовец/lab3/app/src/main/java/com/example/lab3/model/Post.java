package com.example.lab3.model;

import com.google.gson.annotations.SerializedName;

public class Post implements Displayable {

    @SerializedName("userId")
    private int userId;

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    public Post() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body != null ? body : ""; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String getSubtitle() {
        return "User ID: " + userId;
    }

    @Override
    public String getDetailInfo() {
        return "ID: " + id + "\n" +
               "User ID: " + userId + "\n\n" +
               "Заголовок:\n" + getTitle() + "\n\n" +
               "Текст:\n" + getBody();
    }

    @Override
    public String getImageUrl() { return null; }

    @Override
    public String toCsvRow() {
        return id + "," + userId + ",\"" +
               getTitle().replace("\"", "\"\"") + "\",\"" +
               getBody().replace("\"", "\"\"").replace("\n", " ") + "\"";
    }

    @Override
    public String getTypeName() { return "Post"; }
}
