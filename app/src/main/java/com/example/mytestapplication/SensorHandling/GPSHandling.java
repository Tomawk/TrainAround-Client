package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class GPSHandling {

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double totalDistance = 0;
    private double prevLat = 0;
    private double prevLon = 0;
    private Context context;

    public static final long UPDATE_INTERVAL = 1000; //in milliseconds
    public static final long FASTEST_INTERVAL = 500;
    public static final long MAX_WAIT_TIME = 500;
    public static final float UPDATE_AFTER_METERS = 1; //in meters

    public GPSHandling (Context ctx){

        context = ctx;

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
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        //first gps location
                        if(prevLat == 0 && prevLon == 0) {
                            prevLat = latitude;
                            prevLon = longitude;
                        } else {
                            Location prevLoc = new Location("");
                            prevLoc.setLatitude(prevLat);
                            prevLoc.setLongitude(prevLon);
                            totalDistance += prevLoc.distanceTo(location);
                            prevLat = latitude;
                            prevLon = longitude;
                        }
                        float speed = location.getSpeed();
                        boolean hasSpeed = location.hasSpeed();
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
        TextView textView_activity = (TextView) ((Activity)context).findViewById(R.id.textView_gps);
        textView_activity.setText("Lat: " + latitude +"  " +
                "Lon: " + longitude + "  " +
                "Speed: " + speed);
        textView_activity.setTextColor(Color.GREEN);
        Log.e("GPSHandling","hasSpeed - " + hasSpeed);
        TextView textView_distance = (TextView) ((Activity)context).findViewById(R.id.distance_view);
        textView_distance.setText("Distance: " + totalDistance);
        textView_distance.setTextColor(Color.GREEN);
    }
}
