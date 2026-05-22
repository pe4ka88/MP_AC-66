package com.example.mangalistapp;

public class Manga {

    private String title;
    private String description;
    private String coverUrl;
    private String genres;
    private String rating;

    public Manga(String title,
                 String description,
                 String coverUrl,
                 String genres,
                 String rating) {

        this.title = title;
        this.description = description;
        this.coverUrl = coverUrl;
        this.genres = genres;
        this.rating = rating;
    }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getCoverUrl() { return coverUrl; }

    public String getGenres() { return genres; }

    public String getRating() { return rating; }
}
