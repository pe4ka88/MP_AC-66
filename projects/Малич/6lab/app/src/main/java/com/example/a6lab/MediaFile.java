package com.example.a6lab;

public class MediaFile {
    private String name;
    private String uriString;
    private String type;

    public MediaFile(String name, String uriString, String type) {
        this.name = name;
        this.uriString = uriString;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getUriString() {
        return uriString;
    }

    public String getType() {
        return type;
    }
}