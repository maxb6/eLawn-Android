package com.example.elawn_android;

public class Coordinate {

    private double lat,lon;

    public Coordinate(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;

    }

    public Coordinate ()
    {
    }

    public double getLat()
    {
        return lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLat(double nlat){
        lat = nlat;
    }

    public void setLon(double nLon)
    {
        lon = nLon;
    }
}
