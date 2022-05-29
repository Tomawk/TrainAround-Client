package com.example.mytestapplication;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mytestapplication.SensorHandling.GPSHandling;
import com.example.mytestapplication.SensorHandling.HeartRateHandling;
import com.example.mytestapplication.SensorHandling.SensorUtility;
import com.example.mytestapplication.SensorHandling.StepCounterHandling;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

public class SensorActivity extends Activity {

    private static final String TAG = SensorActivity.class.getSimpleName();

    private FusedLocationProviderClient fusedLocationClient;
    private GPSHandling gpsHandling;
    private StepCounterHandling stepCounterHandling;
    private HeartRateHandling heartRateHandling;
    private boolean locationUpdating = false;
    private boolean stepCounterUpdating = false;
    private boolean heartRateUpdating = false;
    private boolean activityUpdating = false;
    private int seconds = 0;
    private String currentActivity = "";


    private Intent intent;
    private PendingIntent pendingIntent;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("ResumeGPS")){
                enableGPSLocations();
            }else{
                // Get extra data included in the Intent
                String message = intent.getStringExtra("Activity");
                if(message.equals("STILL") && !currentActivity.equals("STILL")){ //previous activity is not still and detected one is still
                    stepCounterHandling.setStepsAtStill();
                    disableGPSLocations();
                } else if(message.equals("STILL") && currentActivity.equals("STILL")){  //not a transition
                    //nothing must be done here
                } else { //previous activity is still and
                    enableGPSLocations(); //previous activity is not still and detected one is still
                }

                currentActivity = message;
                TextView textView_activity = (TextView) findViewById(R.id.textView_activity);
                textView_activity.setText("Activity Recognized: " + message);
                textView_activity.setTextColor(Color.GREEN);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        gpsHandling = new GPSHandling(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.requestLocationUpdates(gpsHandling.getLocationRequest(),gpsHandling.getLocationCallback(), Looper.getMainLooper());
        locationUpdating = true;

        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Display all the available sensors on the current tested device, debug purpose
        String available_sensor_list = SensorUtility.getSensorList(sensorManager);
        Log.d(TAG,available_sensor_list);

        intent = new Intent( this, ActivityRecognizedService.class );
        pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.getClient(this).requestActivityUpdates(0, pendingIntent);
        activityUpdating = true;

        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction("ActivityRecognized");
        broadcastFilter.addAction("ResumeGPS");

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver,broadcastFilter);

        //Sensor classes instantiations
        stepCounterHandling = new StepCounterHandling(sensorManager, this);
        heartRateHandling = new HeartRateHandling(sensorManager,this);

        View stop_activity_btn = findViewById(R.id.stop_activity_btn);
        stop_activity_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                stopSensorActivity();
            }
        });

        //Timer
        runTimer();
    }

    public void onResume(){
        super.onResume();
        if (!currentActivity.equals("STILL")){
            //enableStepCounter();
            enableGPSLocations();
        }
        enableStepCounter();
        enableHeartRate();
        enableActivityRecognition();
    }

    public void onPause(){
        super.onPause();
    }

    private void runTimer()
    {

        // Get the text view.
        final TextView timeView
                = (TextView)findViewById(
                R.id.textView_stopwatch);

        // Creates a new Handler
        final Handler handler
                = new Handler();

        // Call the post() method, passing in a new Runnable.
        // The post() method processes code without a delay,
        // so the code in the Runnable will run almost immediately.
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

                // Set the text view text
                timeView.setText("Timer: " + time);
                timeView.setTextColor(Color.BLACK);

                // If running is true, increment the seconds variable
                if (true) {
                    seconds++;
                }

                // Post the code again with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void enableGPSLocations(){
        if(locationUpdating == false){
            TextView textView_gps = (TextView) findViewById(R.id.textView_gps);
            textView_gps.setText("GPS: (not working or loading)");
            textView_gps.setTextColor(Color.BLACK);
            TextView textView_distance = (TextView) findViewById(R.id.distance_view);
            textView_distance.setText("Distance: (not working or loading)");
            textView_distance.setTextColor(Color.BLACK);
            TextView textView_pace = (TextView) findViewById(R.id.pace_view);
            textView_pace.setText("Pace: (not working or loading)");
            textView_pace.setTextColor(Color.BLACK);
            fusedLocationClient.requestLocationUpdates(gpsHandling.getLocationRequest(),gpsHandling.getLocationCallback(), Looper.getMainLooper());
            locationUpdating = true;
            Log.i("SensorActivity","GPS correctly enabled!");
        } else{
            Log.w(TAG,"GPS already enabled!");
        }
    }

    @SuppressLint("MissingPermission")
    public void disableGPSLocations(){
        if(locationUpdating == true){
            fusedLocationClient.removeLocationUpdates(gpsHandling.getLocationCallback());
            locationUpdating = false;
            TextView textView_gps = (TextView) findViewById(R.id.textView_gps);
            textView_gps.setText("GPS sensor is currently stopped to save battery");
            textView_gps.setTextColor(Color.RED);
            TextView textView_distance = (TextView) findViewById(R.id.distance_view);
            textView_distance.append(" (Currently stopped)");
            textView_distance.setTextColor(Color.RED);
            TextView textView_pace = (TextView) findViewById(R.id.pace_view);
            textView_pace.append(" (Currently stopped)");
            textView_pace.setTextColor(Color.RED);
            Log.i("SensorActivity","GPS correctly disabled!");
        } else{
            Log.w(TAG,"GPS already disabled!");
        }
    }

    //When the user is STILL all unnecessary sensors should be stopped
    public void stopStepCounter(){
        if(stepCounterUpdating == true){
            stepCounterHandling.stopSensor();
            TextView textView_steps = (TextView) findViewById(R.id.textView_steps);
            textView_steps.append(" (Currently Stopped)");
            textView_steps.setTextColor(Color.RED);
            stepCounterUpdating = false;
            Log.i("SensorActivity", "StepCounter sensor stopped!");
        } else{
            Log.w(TAG, "StepCounter sensor already stopped!");
        }
    }

    public void enableStepCounter(){
        if(stepCounterUpdating == false){
            stepCounterHandling.enableSensor();
            TextView textView_steps = (TextView) findViewById(R.id.textView_steps);
            textView_steps.setText("Step counter: (not working or loading)");
            textView_steps.setTextColor(Color.BLACK);
            stepCounterUpdating = true;
            Log.i("SensorActivity", "StepCounter sensor enabled!");
        } else{
            Log.w("SensorActivity", "StepCounter sensor already enabled!");
        }
    }

    public void stopHeartRate(){
        if(heartRateUpdating == true){
            heartRateHandling.stopSensor();
            TextView textView_steps = (TextView) findViewById(R.id.textView_heart);
            textView_steps.setText("HeartRate sensor is currently stopped to save battery");
            textView_steps.setTextColor(Color.RED);
            heartRateUpdating = false;
            Log.i("SensorActivity", "HeartRate sensor stopped!");
        } else{
            Log.w("SensorActivity", "HeartRate sensor already stopped!");
        }
    }

    public void enableHeartRate(){
        if(heartRateUpdating == false){
            heartRateHandling.enableSensor();
            TextView textView_heart = (TextView) findViewById(R.id.textView_heart);
            textView_heart.setText("Heart Rate: (not working or loading)");
            textView_heart.setTextColor(Color.BLACK);
            heartRateUpdating = true;
            Log.i("SensorActivity", "HeartRate sensor enabled!");
        } else{
            Log.w("SensorActivity", "HeartRate sensor already enabled!");
        }
    }

    public void stopActivityRecognition(){
        if(activityUpdating == true){
            ActivityRecognition.getClient(this).removeActivityUpdates(pendingIntent);
            TextView textView_activity = (TextView) findViewById(R.id.textView_activity);
            textView_activity.setText("Activity Recognition is currently stopped to save battery");
            textView_activity.setTextColor(Color.RED);
            Log.i(TAG, "Activity Recognition stopped!");
            activityUpdating = false;
        } else {
            Log.w("SensorActivity", "Activity Recognition already stopped!");
        }
    }

    public void enableActivityRecognition(){
        if(activityUpdating == false){
            intent = new Intent( this, ActivityRecognizedService.class );
            pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
            ActivityRecognition.getClient(this).requestActivityUpdates(0,pendingIntent);
            TextView textView_activity = (TextView) findViewById(R.id.textView_activity);
            textView_activity.setText("Activity Recognized: (not working or loading)");
            textView_activity.setTextColor(Color.BLACK);
            activityUpdating = true;
            Log.i("SensorActivity", "Activity Recognition enabled!");
        } else{
            Log.w("SensorActivity", "Activity Recognition already enabled!");
        }
    }

    public void stopSensorActivity(){
        Log.d(TAG, "Stopping sensors monitoring activity ...");
        finish();
    }
/*
    private void sendStillnessAlert() {
        Intent intent = new Intent("StillnessAlert");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }*/

    @Override
    public void onDestroy(){
        stopActivityRecognition();
        stopStepCounter();
        stopHeartRate();
        disableGPSLocations();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //stepCounterHandling.onDestroy();
        super.onDestroy();
    }
}