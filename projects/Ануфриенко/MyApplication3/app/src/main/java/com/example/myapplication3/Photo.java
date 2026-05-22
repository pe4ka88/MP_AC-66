package com.example.myapplication3;

public class Photo {
    private int id;
    private String title;
    private String url;
    private String thumbnailUrl;

    // источник картинок — меняется из ListFragment
    public static String imageSource = "picsum";

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        switch (imageSource) {
            case "dummyimage":
                return "https://dummyimage.com/150x150/cccccc/000000.png&text=" + id;
            case "placeholder":
                String color = String.format("%06x", (id * 1234567) & 0xFFFFFF);
                return "https://placehold.co/150x150/" + color + "/ffffff/png";
            default:
                return "https://picsum.photos/seed/" + id + "/150/150";
        }
    }

    public String getUrl() {
        switch (imageSource) {
            case "dummyimage":
                return "https://dummyimage.com/600x600/cccccc/000000.png&text=" + id;
            case "placeholder":
                return "https://placehold.co/600x600/png";
            default:
                return "https://picsum.photos/seed/" + id + "/600/600";
        }
    }
}