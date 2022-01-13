package com.example.elawn_android;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class PathFinding {

    Coordinate v1, v2, v3, v4; // = new Coordinate();

    Queue<Coordinate> path;
    double mowerWidth = 1;

    public Coordinate getMidpoint(Coordinate c1, Coordinate c2)
    {
        //midpoint between 2 coordinates
        float lat1 = c1.getLat();
        float lat2 = c2.getLat();
        float lon1 = c1.getLon();
        float lon2 = c2.getLon();

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = (float) Math.toRadians(lat1);
        lat2 = (float) Math.toRadians(lat2);
        lon1 = (float) Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        Coordinate midpoint = new Coordinate(Math.toDegrees(lat3), Math.toDegrees(lon3));

        return midpoint;
    }

    public double getDistance(Coordinate c1, Coordinate c2)
    {
        final int R = 6371; // Radius of the earth

        float lat1 = c1.getLat();
        float lat2 = c2.getLat();
        float lon1 = c1.getLon();
        float lon2 = c2.getLon();

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    public void pathAlgorithm()
    {
        path.add(v1);
        path.add(v2);

        Coordinate midpoint;
        List<Coordinate> mids = new ArrayList<Coordinate>();

        for(int i =0; i<=1; i++){
            if(getDistance(v1,v2) > mowerWidth){
                midpoint = getMidpoint(v1, v2);

                mids.add(midpoint);
                //something recursive should be going on here
            }
            else{

            }
        }


        if(getDistance(v2,v3) > mowerWidth)
        {

        }

    }
}
