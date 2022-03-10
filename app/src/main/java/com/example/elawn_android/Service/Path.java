package com.example.elawn_android.Service;

import androidx.annotation.NonNull;

public class Path {
    private long id;
    private String pathName;
    private String pathAddress;
    private Coordinate v1;
    private Coordinate v2;
    private Coordinate v3;
    private Coordinate v4;
    private Coordinate vCharge;


    public Path() {
    }

    public Path(long id,String pathName, String pathAddress) {
        this.id = id;
        this.pathName = pathName;
        this.pathAddress = pathAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getPathAddress() {
        return pathAddress;
    }

    public void setPathAddress(String pathAddress) {
        this.pathAddress = pathAddress;
    }

    public Coordinate getV1() {
        return v1;
    }

    public void setV1(Coordinate v1) {
        this.v1 = v1;
    }

    public Coordinate getV2() {
        return v2;
    }

    public void setV2(Coordinate v2) {
        this.v2 = v2;
    }

    public Coordinate getV3() {
        return v3;
    }

    public void setV3(Coordinate v3) {
        this.v3 = v3;
    }

    public Coordinate getV4() {
        return v4;
    }

    public void setV4(Coordinate v4) {
        this.v4 = v4;
    }

    public Coordinate getVCharge() {
        return vCharge;
    }

    public void setVCharge(Coordinate vCharge) {
        this.vCharge = vCharge;
    }

    @Override
    public String toString() {
        return "Path{" +
                "id=" + id +
                ", pathName='" + pathName + '\'' +
                ", pathAddress='" + pathAddress + '\'' +
                '}';
    }
}
