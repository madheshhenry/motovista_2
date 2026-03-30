package com.example.motovista_deep.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BikeVariantModel implements Parcelable, Serializable {
    @SerializedName("variant_name")
    public String variantName;

    @SerializedName("price_details")
    public PriceDetails priceDetails;

    @SerializedName("brakes_wheels")
    public BrakesWheels brakesWheels;

    @SerializedName("colors")
    public List<VariantColor> colors;

    @SerializedName("custom_sections")
    public List<CustomSection> customSections;

    // Constructor
    public BikeVariantModel() {
        priceDetails = new PriceDetails();
        brakesWheels = new BrakesWheels();
        colors = new ArrayList<>();
        customSections = new ArrayList<>();
    }

    protected BikeVariantModel(Parcel in) {
        variantName = in.readString();
        priceDetails = in.readParcelable(PriceDetails.class.getClassLoader());
        brakesWheels = in.readParcelable(BrakesWheels.class.getClassLoader());
        colors = in.createTypedArrayList(VariantColor.CREATOR);
        customSections = in.createTypedArrayList(CustomSection.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(variantName);
        dest.writeParcelable(priceDetails, flags);
        dest.writeParcelable(brakesWheels, flags);
        dest.writeTypedList(colors);
        dest.writeTypedList(customSections);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BikeVariantModel> CREATOR = new Creator<BikeVariantModel>() {
        @Override
        public BikeVariantModel createFromParcel(Parcel in) {
            return new BikeVariantModel(in);
        }

        @Override
        public BikeVariantModel[] newArray(int size) {
            return new BikeVariantModel[size];
        }
    };

    // --- Inner Classes ---

    public static class PriceDetails implements Parcelable, Serializable {
        @SerializedName("ex_showroom")
        public String exShowroom;
        @SerializedName("insurance")
        public String insurance;
        @SerializedName("registration")
        public String registration;
        @SerializedName("ltrt")
        public String ltrt;
        @SerializedName("total_on_road")
        public String totalOnRoad;

        public PriceDetails() {}

        protected PriceDetails(Parcel in) {
            exShowroom = in.readString();
            insurance = in.readString();
            registration = in.readString();
            ltrt = in.readString();
            totalOnRoad = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(exShowroom);
            dest.writeString(insurance);
            dest.writeString(registration);
            dest.writeString(ltrt);
            dest.writeString(totalOnRoad);
        }

        @Override
        public int describeContents() { return 0; }

        public static final Creator<PriceDetails> CREATOR = new Creator<PriceDetails>() {
            @Override
            public PriceDetails createFromParcel(Parcel in) { return new PriceDetails(in); }
            @Override
            public PriceDetails[] newArray(int size) { return new PriceDetails[size]; }
        };
    }

    public static class BrakesWheels implements Parcelable, Serializable {
        @SerializedName("front_brake")
        public String frontBrake;
        @SerializedName("rear_brake")
        public String rearBrake;
        @SerializedName("braking_system")
        public String brakingSystem;
        @SerializedName("wheel_type")
        public String wheelType;

        public BrakesWheels() {}

        protected BrakesWheels(Parcel in) {
            frontBrake = in.readString();
            rearBrake = in.readString();
            brakingSystem = in.readString();
            wheelType = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(frontBrake);
            dest.writeString(rearBrake);
            dest.writeString(brakingSystem);
            dest.writeString(wheelType);
        }

        @Override
        public int describeContents() { return 0; }

        public static final Creator<BrakesWheels> CREATOR = new Creator<BrakesWheels>() {
            @Override
            public BrakesWheels createFromParcel(Parcel in) { return new BrakesWheels(in); }
            @Override
            public BrakesWheels[] newArray(int size) { return new BrakesWheels[size]; }
        };
    }

    public static class VariantColor implements Parcelable, Serializable {
        @SerializedName("color_name")
        public String colorName;
        @SerializedName("color_hex")
        public String colorHex;
        @SerializedName("image_paths")
        public List<String> imagePaths;
        
        public transient List<String> tempImageUris;

        public VariantColor() {
            imagePaths = new ArrayList<>();
            tempImageUris = new ArrayList<>();
        }

        protected VariantColor(Parcel in) {
            colorName = in.readString();
            colorHex = in.readString();
            imagePaths = in.createStringArrayList();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(colorName);
            dest.writeString(colorHex);
            dest.writeStringList(imagePaths);
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VariantColor that = (VariantColor) o;
            if (colorName != null ? !colorName.equals(that.colorName) : that.colorName != null) return false;
            if (colorHex != null ? !colorHex.equals(that.colorHex) : that.colorHex != null) return false;
            return imagePaths != null ? imagePaths.equals(that.imagePaths) : that.imagePaths == null;
        }

        @Override
        public int hashCode() {
            int result = colorName != null ? colorName.hashCode() : 0;
            result = 31 * result + (colorHex != null ? colorHex.hashCode() : 0);
            result = 31 * result + (imagePaths != null ? imagePaths.hashCode() : 0);
            return result;
        }

        public static final Creator<VariantColor> CREATOR = new Creator<VariantColor>() {
            @Override
            public VariantColor createFromParcel(Parcel in) { return new VariantColor(in); }
            @Override
            public VariantColor[] newArray(int size) { return new VariantColor[size]; }
        };
    }

    public static class CustomSection implements Parcelable, Serializable {
        @SerializedName("section_name")
        public String sectionName;
        @SerializedName("fields")
        public List<CustomField> fields;

        public CustomSection() {
            fields = new ArrayList<>();
        }
        
        public CustomSection(String name) {
            this.sectionName = name;
            this.fields = new ArrayList<>();
        }

        protected CustomSection(Parcel in) {
            sectionName = in.readString();
            fields = in.createTypedArrayList(CustomField.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(sectionName);
            dest.writeTypedList(fields);
        }

        @Override
        public int describeContents() { return 0; }

        public static final Creator<CustomSection> CREATOR = new Creator<CustomSection>() {
            @Override
            public CustomSection createFromParcel(Parcel in) { return new CustomSection(in); }
            @Override
            public CustomSection[] newArray(int size) { return new CustomSection[size]; }
        };
    }

    public static class CustomField implements Parcelable, Serializable {
        @SerializedName("key")
        public String key;
        @SerializedName("value")
        public String value;

        public CustomField(String k, String v) {
            this.key = k;
            this.value = v;
        }

        protected CustomField(Parcel in) {
            key = in.readString();
            value = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(key);
            dest.writeString(value);
        }

        @Override
        public int describeContents() { return 0; }

        public static final Creator<CustomField> CREATOR = new Creator<CustomField>() {
            @Override
            public CustomField createFromParcel(Parcel in) { return new CustomField(in); }
            @Override
            public CustomField[] newArray(int size) { return new CustomField[size]; }
        };
    }
}
