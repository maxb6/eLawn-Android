package com.example.elawn_android;

import java.util.*;

public class PathFinding {

    Coordinate v1, v2, v3, v4; // = new Coordinate();

    ArrayList<Coordinate> path = new ArrayList<>();
    ArrayList<Coordinate> topPath = new ArrayList<>();
    ArrayList<Coordinate> bottomPath = new ArrayList<>();


    double mowerWidth = 5; //Width of mower in meters

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
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        Coordinate midpoint = new Coordinate(Math.toDegrees(lat3), Math.toDegrees(lon3));

        return midpoint;
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
        // topPath.add(0,v2);
        //topPath.add(v3);
        fillBottomLeft(v1,v4);
        bottomPath.add(0,v1);
        bottomPath.add(v4);


        for(int i = 0; i< topPath.size()-1;i++)
        {
            for(int j = i+1; j< topPath.size()-1; j++)
            {
                if(topPath.get(i).getLon() > topPath.get(j).getLon())
                {
                    Collections.swap(topPath,i,j);
                }
            }
        }

        for(int i = 0; i< bottomPath.size()-1;i++)
        {
            for(int j = i+1; j< bottomPath.size(); j++)
            {
                if((double)bottomPath.get(i).getLon() > (double)bottomPath.get(j).getLon())
                {
                    Collections.swap(bottomPath,i,j);
                }
            }
        }




        int maxSize;

        if(topPath.size() > bottomPath.size())
        {
            maxSize = topPath.size();
        }
        else
        {
            maxSize = bottomPath.size();
        }

        path.add(topPath.get(0));
        int j = 0;

        for(int i = 1; i<maxSize-1; i++)
        {

            if(j< bottomPath.size()-1)
            {
                path.add(bottomPath.get(j++));
                path.add(bottomPath.get(j++));
            }
            else if(j >= bottomPath.size())
            {
                path.add(bottomPath.get(bottomPath.size()-1));
            }

            if(i<topPath.size()-1)
            {
                path.add(topPath.get(i++));
                path.add(topPath.get(i));
            }

            else if(i>=topPath.size())
            {
                path.add(topPath.get(topPath.size()-1));
            }

        }


        path.add(0,v2);
        path.add(0,v1);
        path.add(v3);
        path.add(v4);

        for (Coordinate s : path) {
            System.out.println(s.toString());
        }


        return path;



    }
}