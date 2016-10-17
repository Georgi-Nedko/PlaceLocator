package com.example.xcomputers.placelocator.model;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class MyPlace implements Serializable{
    private String name;
    private String address;
    private float rating;
    private String placeID;
    private String distanceToPhone;
    private Location location;
    private String duration;

    public MyPlace(String name, String address, float rating, String placeID, Location location, String distanceToPhone, String duration) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.placeID = placeID;
        this.location = location;
        this.distanceToPhone = distanceToPhone;
        this.duration = duration;
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

    public String getDistanceToPhone() {
        return distanceToPhone;
    }

    public String getDuration() {
        return duration;
    }
}
