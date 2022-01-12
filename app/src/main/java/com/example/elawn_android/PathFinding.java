package com.example.elawn_android;

public class PathFinding {




    public Coordinate getMidpoint(Coordinate c1, Coordinate c2)
    {
        // Returns midpoint between 2 coordinates
        return c2;
    }

    public double getDistance(Coordinate c1, Coordinate c2)
    {
        double distance = c1.getLat()-c2.getLat(); // Not right, just an example

        return distance;
    }
}
