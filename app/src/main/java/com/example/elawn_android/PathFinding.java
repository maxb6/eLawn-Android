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

    public Coordinate movedCoordinate(Coordinate c1, Double distance, Double bearing)
    {
        float nLat= (float)(c1.getLat() + (Math.sin(Math.toRadians(bearing)) * distance));
        float nLon = (float)(c1.getLon() + (Math.sin(Math.toRadians(bearing)) * distance));

        Coordinate nc = new Coordinate(nLat, nLon);

        return nc;
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

    public double bearing(Coordinate c1, Coordinate c2){

        float lat1 = c1.getLat();
        float lat2 = c2.getLat();
        float lon1 = c1.getLon();
        float lon2 = c2.getLon();

        double longitude1 = lon1;
        double longitude2 = lon2;
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(longitude2-longitude1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    public void pathAlgorithm() {

        path.add(v1);
        path.add(v2);

        double distance_v2_v3 = getDistance(v2, v3);
        int numb_midpoints_v2_v3 = (int) Math.ceil(distance_v2_v3 / mowerWidth);
        double bearing_v2_v3 = bearing(v2, v3);

        List<Coordinate> topCoordinates = new ArrayList<Coordinate>();

        for (int i = 0; i < numb_midpoints_v2_v3; i++) {
            Coordinate currentPoint;
            Coordinate newPoint;

            currentPoint = v2;
            newPoint = movedCoordinate(currentPoint, mowerWidth, bearing_v2_v3);
            topCoordinates.add(newPoint);
            currentPoint = newPoint;
        }

        double distance_v1_v4 = getDistance(v1, v4);
        int numb_midpoints_v1_v4 = (int) Math.ceil(distance_v1_v4 / mowerWidth);
        double bearing_v1_v4 = bearing(v1, v4);

        List<Coordinate> bottomCoordinates = new ArrayList<Coordinate>();

        for (int i = 0; i < numb_midpoints_v1_v4; i++) {
            Coordinate currentPoint;
            Coordinate newPoint;
            currentPoint = v1;
            newPoint = movedCoordinate(currentPoint, mowerWidth, bearing_v1_v4);
            bottomCoordinates.add(newPoint);
            currentPoint = newPoint;
        }

        int total_points = topCoordinates.size() + bottomCoordinates.size();

        path.add(topCoordinates.get(1));
        path.add(bottomCoordinates.get(1));
        path.add(bottomCoordinates.get(2));

        int j = 2;

        for (int i = 2; i <= total_points; i++)
        {
            if(i>topCoordinates.size())
            {
                path.add(topCoordinates.get(topCoordinates.size()-1));
            }
            else if(i<topCoordinates.size())
            {
                path.add(topCoordinates.get(i));
                path.add(topCoordinates.get(++i));
            }

            if(j>bottomCoordinates.size())
            {
                path.add(topCoordinates.get(bottomCoordinates.size()-1));
            }
            else if(j<bottomCoordinates.size())
            {
                path.add(bottomCoordinates.get(++j));
                path.add(bottomCoordinates.get(++j));
            }
        }

        //Populate Firebase
    }
}
