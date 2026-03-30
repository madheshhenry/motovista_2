package com.example.motovista_deep.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomFitting implements Parcelable {
    @com.google.gson.annotations.SerializedName("name")
    private String name;
    
    @com.google.gson.annotations.SerializedName("price")
    private String price;

    @com.google.gson.annotations.SerializedName("is_mandatory")
    private boolean isMandatory;

    public CustomFitting(String name, String price, boolean isMandatory) {
        this.name = name;
        this.price = price;
        this.isMandatory = isMandatory;
    }

    public CustomFitting(String name, String price) {
        this(name, price, false);
    }

    protected CustomFitting(Parcel in) {
        name = in.readString();
        price = in.readString();
        isMandatory = in.readByte() != 0;
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
        dest.writeString(price);
        dest.writeByte((byte) (isMandatory ? 1 : 0));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }
}
