package com.alterloop.util;

import java.text.DecimalFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormat;

/**
 * Created by Yash on 10/28/2014.
 */

/* Class to calculate shortest globe distance between (lat1, lng1) and (lat2, lng2)  */
public class DistanceAlgorithm {



    /* default constructor */
    public DistanceAlgorithm()
    {

    }

    /* This method uses Haversine formula , which makes use of a neglected Haversine
     * Trigonometric function, haversine(A)=(1-cos(A))/2 = sin^2(A/2)
     * Input: lat1, lng1, lat2, ln2
     * Output: distance between above points in meters
     */
    public static double DistanceBetweenPoints(double lat1, double lng1, double lat2, double lng2)
    {
        double earthRadius = 3958.75;   // In miles 3958.75 miles ~ 6378.16 Km

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;

	    /* In order to have two digits after decimal */
        DecimalFormat newFormat = new DecimalFormat("#.##");
        double distance =  Double.valueOf(newFormat.format(dist * meterConversion));
        //double distance =  Double.valueOf(dist * meterConversion);
        return Double.valueOf(distance/1000);

    }


}
