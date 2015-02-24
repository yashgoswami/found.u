package com.alterloop.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.List;
import java.util.Locale;

/**
 * Created by Yash on 11/18/2014.
 */
/* Class MyLocationListener. Override the methods of LocationListener*/
public class MyLocationListener implements LocationListener {

    Context context;
    StringBuilder strReturnedAddress;
    String address;
    Double lat, lng;

    public MyLocationListener(Context c)
    {
        context=c;
    }
    @Override
    public void onLocationChanged(Location loc) {
        lat = loc.getLatitude();
        lng = loc.getLongitude();

        //*****Address*******
        try {
            //Constructs a Geocoder whose responses will be localized for english
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses != null) {
                strReturnedAddress=new StringBuilder();
                Address returnedAddress = addresses.get(0);
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }

            } else {
            }

            address = strReturnedAddress.toString();
            strReturnedAddress = null;
            if (address.equals("")) {
            }

            if (!address.equals("")) {

                try {
                    SQLiteDatabase db = context.openOrCreateDatabase("FoundApp", context.MODE_PRIVATE, null);
                    db.execSQL("UPDATE RegisteredUserInfo SET  Address='" + address + "', Latitude=" + lat + ", Longitude=" + lng + ";");
                    db.close();

                } catch (Exception ex) {
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            String s=e.getStackTrace().toString();
            String s1=e.getStackTrace().toString();
        }


    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}

