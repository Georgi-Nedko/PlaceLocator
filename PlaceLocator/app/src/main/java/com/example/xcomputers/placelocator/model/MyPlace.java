package com.example.xcomputers.placelocator.model;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class MyPlace {
    private String name;
    private String address;

    public MyPlace(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
