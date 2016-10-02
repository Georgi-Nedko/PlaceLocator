package com.example.xcomputers.placelocator.model;

/**
 * Created by xComputers on 29.9.2016 г..
 */

public class MyPlace {
    private String name;
    private String address;
    private float rating;
    private String placeID;
    private String distanceToPhone;


    public MyPlace(String name, String address, float rating, String placeID, String distanceToPhone) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.placeID = placeID;
        this.distanceToPhone = distanceToPhone;


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
    public String getDistanceToPhone(){
        return distanceToPhone;
    }
}
