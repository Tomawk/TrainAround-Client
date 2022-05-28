package com.example.mytestapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.text.HtmlCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.example.mytestapplication.SensorHandling.AccelerometerHandling;
import com.example.mytestapplication.SensorHandling.GPSHandling;
import com.example.mytestapplication.SensorHandling.HeartRateHandling;
import com.example.mytestapplication.SensorHandling.SensorUtility;
import com.example.mytestapplication.SensorHandling.StepCounterHandling;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

public class SensorActivity extends Activity {

    private FusedLocationProviderClient fusedLocationClient;
    private GPSHandling gpsHandling;
    private AccelerometerHandling accelerometerHandling;
    private StepCounterHandling stepCounterHandling;
    private HeartRateHandling heartRateHandling;
    private boolean locationUpdating = false;
    private boolean sensorsUpdating = true;
    private int seconds = 0;
    private double pace = 0;

    //TODO CHANGE NAME
    private Intent intent;
    private PendingIntent pendingIntent;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Activity");
            if(message.equals("STILL")){
                disableGPSLocations();
                stopAllSensors();
            } else{
                enableGPSLocations();
                enableAllSensors();
            }
            TextView textView_activity = (TextView) findViewById(R.id.textView_activity);
            textView_activity.setText("Activity Recognized: " + message);
            textView_activity.setTextColor(Color.GREEN);
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

        //TODO: Non sono sicuro se basti un sensor manager unico o se servano piu sensor manager per ogni sensore (DA VEDERE)
        SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Display all the available sensors on the current tested device, debug purpose
        String available_sensor_list = SensorUtility.getSensorList(sensorManager);
        Log.d("SensorUtility",available_sensor_list);

        intent = new Intent( this, ActivityRecognizedService.class );
        pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.getClient(this).requestActivityUpdates(0, pendingIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("ActivityRecognized"));

        //Sensor classes instantiations
        accelerometerHandling = new AccelerometerHandling(sensorManager, this);
        stepCounterHandling = new StepCounterHandling(sensorManager, this);
        heartRateHandling = new HeartRateHandling(sensorManager,this);

        //Timer
        runTimer();
    }



    public void onResume(){
        super.onResume();
        enableActivityRecognition();
        enableAllSensors();
        enableGPSLocations();
    }

    public void onPause(){
        super.onPause();
        //TODO: IN QUESTO MODO I SENSORI VENGONO STOPPATI E NON VANNO IN BACKGROUND
        stopActivityRecognition();
        stopAllSensors();
        disableGPSLocations();
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
                if (true) { //TODO ADD A WAY TO STOP THE TIMER (?)
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
        } else{
            Log.e("SensorActivity","GPS already enabled!");
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
        } else{
            Log.e("SensorActivity","GPS already disabled!");
        }
    }

    //When the user is STILL all unnecessary sensors should be stopped
    public void stopAllSensors(){
        if(sensorsUpdating == true){
            accelerometerHandling.onPause();
            stepCounterHandling.onPause();
            heartRateHandling.onPause();
            TextView textView_accl = (TextView) findViewById(R.id.textView_accl);
            textView_accl.setText("Accelerator sensor is currently stopped to save battery");
            textView_accl.setTextColor(Color.RED);
            TextView textView_heart = (TextView) findViewById(R.id.textView_heart);
            textView_heart.setText("Heart Rate sensor is currently stopped to save battery");
            textView_heart.setTextColor(Color.RED);
            TextView textView_steps = (TextView) findViewById(R.id.textView_steps);
            textView_steps.append(" (Currently Stopped)");
            textView_steps.setTextColor(Color.RED);
            sensorsUpdating = false;
        } else{
            Log.e("SensorActivity", "Sensors already stopped");
        }
    }

    public void enableAllSensors(){
        accelerometerHandling.onResume();
        stepCounterHandling.onResume();
        heartRateHandling.onResume();
        TextView textView_accl = (TextView) findViewById(R.id.textView_accl);
        textView_accl.setText("Accelerometer: (not working or loading)");
        textView_accl.setTextColor(Color.BLACK);
        TextView textView_heart = (TextView) findViewById(R.id.textView_heart);
        textView_heart.setText("Heart Rate: (not working or loading)");
        textView_heart.setTextColor(Color.BLACK);
        TextView textView_steps = (TextView) findViewById(R.id.textView_steps);
        textView_steps.setText("Step counter: (not working or loading)");
        textView_steps.setTextColor(Color.BLACK);
        sensorsUpdating = true;
    }

    public void stopActivityRecognition(){
        ActivityRecognition.getClient(this).removeActivityUpdates(pendingIntent);
        TextView textView_activity = (TextView) findViewById(R.id.textView_activity);
        textView_activity.setText("Activity Recognition is currently stopped to save battery");
        textView_activity.setTextColor(Color.RED);
        Log.e("SensorActivity", "Activity Recognition stopped!");
    }

    public void enableActivityRecognition(){
        ActivityRecognition.getClient(this).requestActivityUpdates(0,pendingIntent);
        TextView textView_activity = (TextView) findViewById(R.id.textView_activity);
        textView_activity.setText("Activity Recognized: (not working or loading)");
        textView_activity.setTextColor(Color.BLACK);
        Log.e("SensorActivity", "Activity Recognition enabled!");
    }

    @Override
    public void onDestroy(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}