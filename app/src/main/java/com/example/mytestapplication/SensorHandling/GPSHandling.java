package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.R;
import com.example.mytestapplication.Transactions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class GPSHandling {

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double latitude;
    private double longitude;
    private float speed;
    private boolean hasSpeed;
    private Context context;

    public static final long UPDATE_INTERVAL = 1000; //in milliseconds
    public static final long FASTEST_INTERVAL = 500;
    public static final long MAX_WAIT_TIME = 500;
    // public static final float UPDATE_AFTER_METERS = 50; //in meters

    public GPSHandling (Context ctx){

        context = ctx;

        locationRequest = LocationRequest.create()
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                //.setSmallestDisplacement(UPDATE_AFTER_METERS) //TODO: DEBUG ONLY
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
                        speed = location.getSpeed();
                        hasSpeed = location.hasSpeed();
                        printLocation(latitude,longitude,speed,hasSpeed);
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

    public void printLocation(double latitude,double longitude,float speed,boolean hasSpeed){
        TextView textView_print = (TextView) ((Activity)context).findViewById(R.id.textView_gps);
        textView_print.setText("Lat: " + latitude +"  " +
                "Lon: " + longitude + "  " +
                "Speed: " + speed);
        Log.e("GPSHandling","hasSpeed - " + hasSpeed);
    }
}
