package com.example.xcomputers.placelocator.model;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class Category {
    private String name;
    private int image;

    public Category(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}
