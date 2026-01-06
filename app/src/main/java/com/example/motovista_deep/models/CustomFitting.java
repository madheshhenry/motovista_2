package com.example.motovista_deep.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomFitting implements Parcelable {
    @com.google.gson.annotations.SerializedName("name")
    private String name;
    
    @com.google.gson.annotations.SerializedName("price")
    private double price;

    public CustomFitting(String name, double price) {
        this.name = name;
        this.price = price;
    }

    protected CustomFitting(Parcel in) {
        name = in.readString();
        price = in.readDouble();
    }

    public static final Creator<CustomFitting> CREATOR = new Creator<CustomFitting>() {
        @Override
        public CustomFitting createFromParcel(Parcel in) {
            return new CustomFitting(in);
        }

        @Override
        public CustomFitting[] newArray(int size) {
            return new CustomFitting[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
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
}
