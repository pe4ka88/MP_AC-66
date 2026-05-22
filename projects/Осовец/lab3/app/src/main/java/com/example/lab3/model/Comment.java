package com.example.lab3.model;

import com.google.gson.annotations.SerializedName;

public class Comment implements Displayable {

    @SerializedName("postId")
    private int postId;

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("body")
    private String body;

    public Comment() {}

    public int getPostId() { return postId; }

    @Override
    public int getId() { return id; }

    @Override
    public String getTitle() { return name != null ? name : ""; }

    @Override
    public String getSubtitle() { return email != null ? email : ""; }

    @Override
    public String getDetailInfo() {
        return "ID: " + id + "\n" +
               "Post ID: " + postId + "\n\n" +
               "Название:\n" + (name != null ? name : "") + "\n\n" +
               "Email: " + (email != null ? email : "") + "\n\n" +
               "Текст:\n" + (body != null ? body : "");
    }

    @Override
    public String getImageUrl() { return null; }

    @Override
    public String toCsvRow() {
        return id + "," + postId + ",\"" +
               (name != null ? name.replace("\"", "\"\"") : "") + "\",\"" +
               (email != null ? email : "") + "\",\"" +
               (body != null ? body.replace("\"", "\"\"").replace("\n", " ") : "") + "\"";
    }

    @Override
    public String getTypeName() { return "Comment"; }
}
