package com.example.thirdlab8.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * Модель данных пользователя
 * Используется для десериализации JSON и передачи между фрагментами
 */
public class User implements Parcelable {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("website")
    private String website;
    
    @SerializedName("username")
    private String username;
    
    // Вложенный объект компании
    @SerializedName("company")
    private Company company;
    
    // Вложенный объект адреса
    @SerializedName("address")
    private Address address;

    // Конструктор по умолчанию
    public User() {}

    // Конструктор для Parcelable
    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        email = in.readString();
        phone = in.readString();
        website = in.readString();
        username = in.readString();
        company = in.readParcelable(Company.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Геттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name != null ? name : "Unknown";
    }

    public String getEmail() {
        return email != null ? email : "";
    }
    
    public String getUsername() {
        return username != null ? username : "";
    }

    public String getAvatar() {
        // Генерируем аватар на основе ID
        return "https://i.pravatar.cc/150?img=" + (id % 70);
    }

    public String getCompany() {
        return company != null && company.name != null ? company.name : "";
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public String getWebsite() {
        return website != null ? website : "";
    }

    public String getAddress() {
        if (address != null) {
            return address.street + ", " + address.city;
        }
        return "";
    }
    
    public String getFullAddress() {
        if (address != null) {
            return address.street + ", " + address.suite + "\n" + 
                   address.city + ", " + address.zipcode;
        }
        return "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(website);
        dest.writeString(username);
        dest.writeParcelable(company, flags);
        dest.writeParcelable(address, flags);
    }
    
    // Вложенный класс для компании
    public static class Company implements Parcelable {
        @SerializedName("name")
        private String name;
        
        @SerializedName("catchPhrase")
        private String catchPhrase;
        
        @SerializedName("bs")
        private String bs;

        public Company() {}

        protected Company(Parcel in) {
            name = in.readString();
            catchPhrase = in.readString();
            bs = in.readString();
        }

        public static final Creator<Company> CREATOR = new Creator<Company>() {
            @Override
            public Company createFromParcel(Parcel in) {
                return new Company(in);
            }

            @Override
            public Company[] newArray(int size) {
                return new Company[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(name);
            dest.writeString(catchPhrase);
            dest.writeString(bs);
        }
    }
    
    // Вложенный класс для адреса
    public static class Address implements Parcelable {
        @SerializedName("street")
        private String street;
        
        @SerializedName("suite")
        private String suite;
        
        @SerializedName("city")
        private String city;
        
        @SerializedName("zipcode")
        private String zipcode;

        public Address() {}

        protected Address(Parcel in) {
            street = in.readString();
            suite = in.readString();
            city = in.readString();
            zipcode = in.readString();
        }

        public static final Creator<Address> CREATOR = new Creator<Address>() {
            @Override
            public Address createFromParcel(Parcel in) {
                return new Address(in);
            }

            @Override
            public Address[] newArray(int size) {
                return new Address[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(street);
            dest.writeString(suite);
            dest.writeString(city);
            dest.writeString(zipcode);
        }
    }
}
