package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.R;
import com.example.mytestapplication.Transactions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.Locale;

public class GPSHandling {

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private double totalDistance = 0;
    private double paceDistance = 0;
    private double prevLat = 0;
    private double prevLon = 0;
    private Context context;
    private int seconds;

    public static final long UPDATE_INTERVAL = 1000; //in milliseconds
    public static final long FASTEST_INTERVAL = 500;
    public static final long MAX_WAIT_TIME = 500;
    public static final float UPDATE_AFTER_METERS = 3; //in meters

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
                        double pace = 0;
                        //first gps location
                        if(prevLat == 0 && prevLon == 0) {
                            prevLat = latitude;
                            prevLon = longitude;
                        } else {
                            Location prevLoc = new Location("");
                            prevLoc.setLatitude(prevLat);
                            prevLoc.setLongitude(prevLon);
                            totalDistance += prevLoc.distanceTo(location);
                            paceDistance += prevLoc.distanceTo(location);
                            if(paceDistance > 100){ //PACE COMPUTED EVERY 100 METERS with some fluctuations around 100 depending on the gps update
                                pace = seconds/paceDistance;
                                printPace(pace,seconds,paceDistance);
                                paceDistance = 0;
                                seconds = 0;
                            }
                            prevLat = latitude;
                            prevLon = longitude;
                        }
                        float speed = location.getSpeed();
                        boolean hasSpeed = location.hasSpeed();
                        printLocation(latitude,longitude,speed,hasSpeed);
                        printDistance();
                        Transactions.writeDistance(context, totalDistance);
                        if(hasSpeed){
                            Transactions.writeSpeed(context, speed);
                        }
                    }
                }
            }
        };

        runTimer();
    }

    public LocationCallback getLocationCallback() {
        return locationCallback;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void printLocation(double latitude, double longitude, float speed, boolean hasSpeed){
        TextView textView_activity = (TextView) ((Activity)context).findViewById(R.id.textView_gps);
        textView_activity.setText("Lat: " + latitude +"  " +
                "Lon: " + longitude + "  " +
                "Speed: " + speed);
        textView_activity.setTextColor(Color.GREEN);
        Log.e("GPSHandling","hasSpeed - " + hasSpeed);
    }

    public void printDistance(){
        TextView textView_distance = (TextView) ((Activity)context).findViewById(R.id.distance_view);
        textView_distance.setText("Distance: " + totalDistance);
        textView_distance.setTextColor(Color.GREEN);
    }

    public void printPace(double pace,int seconds,double paceDistance){
        TextView textview_pace = (TextView) ((Activity)context).findViewById(R.id.pace_view);
        textview_pace.setText("Pace: " + pace + " PaceDistance: " + paceDistance + " seconds: " + seconds );
        textview_pace.setTextColor(Color.GREEN);
    }

    public void runTimer()
    {

        final Handler handler
                = new Handler();

        handler.post(new Runnable() {
            @Override

            public void run()
            {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes, and seconds.
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);


                if (true) {
                    seconds++;
                }

                // Post the code again with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }
}
