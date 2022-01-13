package com.example.elawn_android;

public class Coordinate {

    private float lat,lon;

    public Coordinate(double lat, double lon)
    {
        this.lat = (float) lat;
        this.lon = (float) lon;

    }

    public float getLat()
    {
        return lat;
    }

    public float getLon()
    {
        return lon;
    }
}
