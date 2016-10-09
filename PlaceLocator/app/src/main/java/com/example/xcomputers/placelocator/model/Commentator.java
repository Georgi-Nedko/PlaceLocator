package com.example.xcomputers.placelocator.model;

/**
 * Created by svetlio on 9.10.2016 г..
 */

public class Commentator {
    String name;
    String commentDescription;
    float rating;
    String time;

    public Commentator(String name, String commentDescription, float rating) {
        this.name = name;
        this.commentDescription = commentDescription;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getCommentDescription() {
        return commentDescription;
    }

    public float getRating() {
        return rating;
    }
}
