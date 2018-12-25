package com.example.pranaykumar.mapboxapp;

import android.location.Location;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String name;
    private double latitude;
    private double longitude;
    private String email;
    private String password;
    private String id;
    private String token;

    public User(String name,double latitude,double longitude,String email,String password,String id,String token){
        this.name=name;
        this.email=email;
        this.password=password;
        this.latitude=latitude;
        this.longitude=longitude;
        this.id=id;
        this.token=token;
    }

    public String getEmail() {
        return email;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}

