package com.rokkhi.rokkhifieldwork;

import com.google.firebase.firestore.GeoPoint;

public class HouseLocation {

    private GeoPoint geo_Point;

    public HouseLocation() {
    }

    public HouseLocation(GeoPoint geo_Point) {
        this.geo_Point = geo_Point;
    }

    public GeoPoint getGeo_Point() {
        return geo_Point;
    }

    public void setGeo_Point(GeoPoint geo_Point) {
        this.geo_Point = geo_Point;
    }
}


