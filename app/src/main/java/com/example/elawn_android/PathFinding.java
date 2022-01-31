package com.example.elawn_android;

import android.util.Log;

import com.example.elawn_android.Coordinate;

import java.util.*;

public class PathFinding {

    Coordinate v1, v2, v3, v4; // = new Coordinate();

    ArrayList<Coordinate> path = new ArrayList<>();
    ArrayList<Coordinate> topPath = new ArrayList<>();
    ArrayList<Coordinate> bottomPath = new ArrayList<>();


    double mowerWidth = 2.5;

    public PathFinding(Coordinate v1, Coordinate v2, Coordinate v3, Coordinate v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;


    }

    public Coordinate getMidpoint(Coordinate c1, Coordinate c2) {
        //midpoint between 2 coordinates
        double lat1 = c1.getLat();
        double lat2 = c2.getLat();
        double lon1 = c1.getLon();
        double lon2 = c2.getLon();

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 =  Math.toRadians(lat1);
        lat2 =  Math.toRadians(lat2);
        lon1 =  Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        Coordinate midpoint = new Coordinate(Math.toDegrees(lat3), Math.toDegrees(lon3));

        return midpoint;
    }

    public Coordinate movedCoordinate(Coordinate c1, Double distance, Double bearing) {
        double nLat = (c1.getLat() + (Math.sin(Math.toRadians(bearing)) * distance));
        double nLon = (c1.getLon() + (Math.cos(Math.toRadians(bearing)) * distance));

        Coordinate nc = new Coordinate(nLat, nLon);

        return nc;
    }

    public double getDistance(Coordinate c1, Coordinate c2) {
        final int R = 6371; // Radius of the earth

        double lat1 = c1.getLat();
        double lat2 = c2.getLat();
        double lon1 = c1.getLon();
        double lon2 = c2.getLon();

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    public double bearing(Coordinate c1, Coordinate c2) {

        double lat1 = c1.getLat();
        double lat2 = c2.getLat();
        double lon1 = c1.getLon();
        double lon2 = c2.getLon();

        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff = Math.toRadians(longitude2 - longitude1);
        double y = Math.sin(longDiff) * Math.cos(latitude2);
        double x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    public void fillTopLeft(Coordinate v22, Coordinate v33) {
        Coordinate midpoint = getMidpoint(v22, v33);

        if ((getDistance(v22, v33) > mowerWidth)) {
            topPath.add((midpoint));
            fillTopLeft(v22, midpoint);
            fillTopLeft(midpoint,v33);
        }

    }



    public void fillBottomLeft(Coordinate v22, Coordinate v33) {
        Coordinate midpoint = getMidpoint(v22, v33);

        if ((getDistance(v22, v33) > mowerWidth)) {
            bottomPath.add((midpoint));
            fillBottomLeft(v22, midpoint);
            fillBottomLeft(midpoint,v33);
        }

    }



    public ArrayList<Coordinate> pathAlgorithm() {


        fillTopLeft(v2, v3);
        topPath.add(0,v2);
        topPath.add(v3);
        fillBottomLeft(v1,v4);
        bottomPath.add(0,v1);
        bottomPath.add(v4);


        for(int i = 0; i< topPath.size()-1;i++)
        {
            for(int j = i+1; j< topPath.size()-1; j++)
            {
                if(topPath.get(i).getLat() > topPath.get(j).getLat())
                {
                    Collections.swap(topPath,i,j);
                }
            }
        }

        for(int i = 0; i< bottomPath.size()-1;i++)
        {
            for(int j = i+1; j< bottomPath.size(); j++)
            {
                if((double)bottomPath.get(i).getLat() > (double)bottomPath.get(j).getLat())
                {
                    Collections.swap(bottomPath,i,j);
                }
            }
        }


        int j = 0;
        for(int i = 0; i<topPath.size()-1; i++)
        {
            path.add(topPath.get(i));
            if(j< topPath.size())
            {
                path.add(bottomPath.get(j++));
                path.add(bottomPath.get(j++));
            }
            path.add(topPath.get(++i));

        }


        for (Coordinate s : path) {
            System.out.println(s.toString());
            //Log.i("PATH-FINDING","Path finding algorithm : LAT:  " + s.getLat() + "--------LON: "+ s.getLon());
        }


        return path;

    }
}
