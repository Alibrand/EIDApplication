package com.ksacp2022.electronicinteriordesignapplication.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.Map;

public class UserProfile {
    String full_name="";
    String region="";
    GeoPoint location;
    String user_type="Normal User";

    public UserProfile() {
    }


    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}
