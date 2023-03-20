package com.ksacp2022.electronicinteriordesignapplication.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
    String id;
    String name;
    String review;
    boolean like=true;
    @ServerTimestamp
    Date created_at=null;

    public Review() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
