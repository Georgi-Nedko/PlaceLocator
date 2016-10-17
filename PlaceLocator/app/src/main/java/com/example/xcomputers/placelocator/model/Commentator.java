package com.example.xcomputers.placelocator.model;

/**
 * Created by svetlio on 9.10.2016 г..
 */

public class Commentator {
    private String name;
    private String commentDescription;
    private float rating;

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
