package com.example.thirdlab9;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Класс модели данных товара для приложения MiniShop
 * 
 * @author Пекун Марк Сергеевич, группа АС-66
 * Лабораторная работа №9. Списки. Создание собственного адаптера
 */
public class Product implements Parcelable {
    private int id;
    private String name;
    private double price;
    private boolean isChecked;
    private String imageUrl;
    private String category;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isChecked = false;
        this.imageUrl = "";
        this.category = "";
    }

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readDouble();
        isChecked = in.readByte() != 0;
        imageUrl = in.readString();
        category = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeString(imageUrl);
        dest.writeString(category);
    }
}
