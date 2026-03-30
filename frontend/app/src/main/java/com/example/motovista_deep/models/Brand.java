package com.example.motovista_deep.models;

public class Brand {
    private int id;
    private String name;
    private int iconResId;
    private int iconBgResId;
    private int iconTintColor;

    public Brand(int id, String name, int iconResId, int iconBgResId, int iconTintColor) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.iconBgResId = iconBgResId;
        this.iconTintColor = iconTintColor;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public int getIconBgResId() { return iconBgResId; }
    public int getIconTintColor() { return iconTintColor; }

    public void setName(String name) { this.name = name; }
}