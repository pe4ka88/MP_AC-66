package com.example.lab3.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Displayable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("website")
    private String website;

    @SerializedName("address")
    private Address address;

    @SerializedName("company")
    private Company company;

    public static class Address implements Serializable {
        @SerializedName("street")
        public String street;
        @SerializedName("suite")
        public String suite;
        @SerializedName("city")
        public String city;
        @SerializedName("zipcode")
        public String zipcode;
        @SerializedName("geo")
        public Geo geo;

        public static class Geo implements Serializable {
            @SerializedName("lat")
            public String lat;
            @SerializedName("lng")
            public String lng;
        }

        @Override
        public String toString() {
            return (street != null ? street : "") + ", " +
                   (suite != null ? suite : "") + ", " +
                   (city != null ? city : "") + " " +
                   (zipcode != null ? zipcode : "");
        }
    }

    public static class Company implements Serializable {
        @SerializedName("name")
        public String name;
        @SerializedName("catchPhrase")
        public String catchPhrase;
        @SerializedName("bs")
        public String bs;

        @Override
        public String toString() {
            return name != null ? name : "";
        }
    }

    public User() {}

    @Override
    public int getId() { return id; }

    @Override
    public String getTitle() { return name != null ? name : ""; }

    @Override
    public String getSubtitle() {
        return "@" + (username != null ? username : "") + " | " + (email != null ? email : "");
    }

    @Override
    public String getDetailInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append("\n");
        sb.append("Имя: ").append(name != null ? name : "").append("\n");
        sb.append("Username: ").append(username != null ? username : "").append("\n");
        sb.append("Email: ").append(email != null ? email : "").append("\n");
        sb.append("Телефон: ").append(phone != null ? phone : "").append("\n");
        sb.append("Сайт: ").append(website != null ? website : "").append("\n");
        if (address != null) {
            sb.append("\nАдрес: ").append(address.toString()).append("\n");
            if (address.geo != null) {
                sb.append("Координаты: ").append(address.geo.lat).append(", ").append(address.geo.lng).append("\n");
            }
        }
        if (company != null) {
            sb.append("\nКомпания: ").append(company.name != null ? company.name : "").append("\n");
            sb.append("Слоган: ").append(company.catchPhrase != null ? company.catchPhrase : "").append("\n");
            sb.append("Сфера: ").append(company.bs != null ? company.bs : "").append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getImageUrl() { return null; }

    @Override
    public String toCsvRow() {
        return id + ",\"" + getTitle().replace("\"", "\"\"") + "\",\"" +
               (username != null ? username : "") + "\",\"" +
               (email != null ? email : "") + "\",\"" +
               (phone != null ? phone : "") + "\",\"" +
               (website != null ? website : "") + "\"";
    }

    @Override
    public String getTypeName() { return "User"; }
}
