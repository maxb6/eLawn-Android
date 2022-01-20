package com.example.elawn_android;

public class Coordinate {

    private float lat,lon;

    public Coordinate(float lat, float lon)
    {
        this.lat = lat;
        this.lon = lon;

    }

    public Coordinate ()
    {
    }

    public float getLat()
    {
        return lat;
    }

    public float getLon()
    {
        return lon;
    }

    public void setLat(float nlat){
        lat = nlat;
    }

    public void setLon(float nLon)
    {
        lon = nLon;
    }
}
