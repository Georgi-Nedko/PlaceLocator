package com.example.xcomputers.placelocator.model;

import android.location.Location;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class MyPlace {
    private String name;
    private String address;
    private float rating;
    private String placeID;
    private String distanceToPhone;
    private Location location;


    public MyPlace(String name, String address, float rating, String placeID, Location location) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.placeID = placeID;
        this.location = location;


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
    public String getID(){
        return this.placeID;
    }
    public Location getLocation(){
        return location;
    }
}
