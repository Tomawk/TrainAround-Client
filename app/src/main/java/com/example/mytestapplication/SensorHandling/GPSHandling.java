package com.example.mytestapplication.SensorHandling;

import android.location.Location;

import com.example.mytestapplication.MainActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class GPSHandling {

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double latitude;
    private double longitude;
    private float speed;

    public static final long UPDATE_INTERVAL = 5000; //in milliseconds
    public static final long FASTEST_INTERVAL = 1000;
    public static final long MAX_WAIT_TIME = 5000;
    public static final float UPDATE_AFTER_METERS = 50; //in meters

    public GPSHandling (MainActivity ma){

        locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setSmallestDisplacement(UPDATE_AFTER_METERS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(MAX_WAIT_TIME);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        speed = location.getSpeedAccuracyMetersPerSecond ();
                        ma.printLocation(latitude,longitude,speed);
                    }
                }
            }
        };
    }

    public LocationCallback getLocationCallback() {
        return locationCallback;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }
}
