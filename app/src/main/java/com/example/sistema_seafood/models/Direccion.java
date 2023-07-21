package com.example.sistema_seafood.models;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Direccion {

    private Context context;
    private Geocoder geocoder;

    public Direccion(Context context) {
        this.context = context;
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public String obtenerDireccion(GeoPoint geoPoint) {
        double latitud = geoPoint.getLatitude();
        double longitud = geoPoint.getLongitude();
        return obtenerDireccion(latitud, longitud);
    }

    public String obtenerDireccion(double latitud, double longitud) {
        String direccionCompleta = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder direccion = new StringBuilder();

                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    direccion.append(address.getAddressLine(i)).append(" ");
                }

                direccionCompleta = direccion.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return direccionCompleta;
    }
}
