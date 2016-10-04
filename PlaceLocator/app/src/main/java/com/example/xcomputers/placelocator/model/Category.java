package com.example.xcomputers.placelocator.model;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class Category {
    private String name;
    private int image;
    private String type;
    public Category(String name, int image, String type) {
        this.name = name;
        this.image = image;
        this.type = type;

    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public String getType() {
        return type;
    }
}
