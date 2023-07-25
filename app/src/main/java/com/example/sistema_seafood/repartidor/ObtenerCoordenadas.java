package com.example.sistema_seafood.repartidor;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;


public class ObtenerCoordenadas {

    private Context context;
    private LocationManager locationManager;
    private double[] coordenadasActuales;

    public ObtenerCoordenadas(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        coordenadasActuales = new double[2];
    }

    public double[] getCoordenadasActuales() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return coordenadasActuales; // Si no tienes permisos, se retorna el arreglo vacío.
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {
            coordenadasActuales[0] = lastKnownLocation.getLatitude();
            coordenadasActuales[1] = lastKnownLocation.getLongitude();
        }

        return coordenadasActuales;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                coordenadasActuales[0] = location.getLatitude();
                coordenadasActuales[1] = location.getLongitude();
                locationManager.removeUpdates(this); // Se detiene la actualización de la ubicación después de obtenerla.
            }
        }
    };
}