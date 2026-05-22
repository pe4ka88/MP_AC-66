package com.example.list.api;

public class Guitar {

    private String name;
    private String image;
    private String summary;
    private String description;

    private float overall_rating;
    private float body_rating;
    private float hardware_rating;
    private float sound_rating;
    private float value_rating;

    public Guitar(String name, String description, String image, String summary,
                  float overall, float body, float hardware, float sound, float value) {

        this.name = name;
        this.description = description;
        this.image = image;
        this.summary = summary;

        this.overall_rating = overall;
        this.body_rating = body;
        this.hardware_rating = hardware;
        this.sound_rating = sound;
        this.value_rating = value;
    }

    public String getName() { return name; }
    public String getImage() { return image; }
    public String getSummary() { return summary; }
    public String getDescription() { return description; }

    public float getOverallRating() { return overall_rating; }
    public float getBodyRating() { return body_rating; }
    public float getHardwareRating() { return hardware_rating; }
    public float getSoundRating() { return sound_rating; }
    public float getValueRating() { return value_rating; }
}