package com.example.sistema_seafood;

import com.google.firebase.firestore.GeoPoint;

public class Ubicacion {
    private double latitud;
    private double longitud;

    public Ubicacion(double latitude, double longitude) {
        this.latitud = latitude;
        this.longitud = longitude;
    }

    public Ubicacion(GeoPoint other) {
        this.latitud = other.getLatitude();
        this.longitud = other.getLongitude();
    }

    public GeoPoint getGeoPoint() {
        return new GeoPoint(latitud, longitud);
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}