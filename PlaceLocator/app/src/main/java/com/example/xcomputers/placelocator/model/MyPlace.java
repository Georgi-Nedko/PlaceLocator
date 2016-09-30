package com.example.xcomputers.placelocator.model;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class MyPlace {
    private String name;
    private String address;
    private float rating;
    public MyPlace(String name, String address, float rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getRating() {
        return rating;
    }
}
