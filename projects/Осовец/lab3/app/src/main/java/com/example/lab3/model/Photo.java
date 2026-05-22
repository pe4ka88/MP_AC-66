package com.example.lab3.model;

import com.google.gson.annotations.SerializedName;

public class Photo implements Displayable {

    @SerializedName("albumId")
    private int albumId;

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    public Photo() {}

    public int getAlbumId() { return albumId; }

    @Override
    public int getId() { return id; }

    @Override
    public String getTitle() { return title != null ? title : ""; }

    @Override
    public String getSubtitle() { return "Album ID: " + albumId; }

    @Override
    public String getDetailInfo() {
        return "ID: " + id + "\n" +
               "Album ID: " + albumId + "\n\n" +
               "Заголовок:\n" + getTitle() + "\n\n" +
               "URL: " + (url != null ? url : "") + "\n" +
               "Thumbnail: " + (thumbnailUrl != null ? thumbnailUrl : "");
    }

    @Override
    public String getImageUrl() { return thumbnailUrl; }

    public String getFullImageUrl() { return url; }

    @Override
    public String toCsvRow() {
        return id + "," + albumId + ",\"" +
               getTitle().replace("\"", "\"\"") + "\",\"" +
               (url != null ? url : "") + "\",\"" +
               (thumbnailUrl != null ? thumbnailUrl : "") + "\"";
    }

    @Override
    public String getTypeName() { return "Photo"; }
}
