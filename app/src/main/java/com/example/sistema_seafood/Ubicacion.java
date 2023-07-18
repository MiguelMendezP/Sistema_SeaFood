package com.example.sistema_seafood;

public class Ubicacion {
    private double latitud,longitud;
public Ubicacion(){}
    public Ubicacion(double latitud, double longitud){
    this.latitud=latitud;
    this.longitud=longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
