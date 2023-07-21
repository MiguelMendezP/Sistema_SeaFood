package com.example.sistema_seafood;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static String getAddressFromLatLng(Context context,double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String addressStr = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                // Concatenate address lines into a single string
                StringBuilder addressBuilder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressBuilder.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        addressBuilder.append(", ");
                    }
                }
                addressStr = addressBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addressStr;
    }
}
