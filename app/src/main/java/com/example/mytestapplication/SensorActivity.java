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

public class SensorActivity extends Activity {

    private FusedLocationProviderClient fusedLocationClient;
    private GPSHandling gpsHandling;
    private AccelerometerHandling accelerometerHandling;
    private StepCounterHandling stepCounterHandling;
    private HeartRateHandling heartRateHandling;
    private boolean locationUpdating = false;
    private boolean sensorsUpdating = true;

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

        Intent intent = new Intent( this, ActivityRecognizedService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.getClient(this).requestActivityUpdates(0, pendingIntent);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("ActivityRecognized"));

        //Sensor classes instantiations
        accelerometerHandling = new AccelerometerHandling(sensorManager, this);
        stepCounterHandling = new StepCounterHandling(sensorManager, this);
        heartRateHandling = new HeartRateHandling(sensorManager,this);
    }


    public void onResume(){
        super.onResume();
        accelerometerHandling.onResume();
        stepCounterHandling.onResume();
        heartRateHandling.onResume();

        if(locationUpdating == false){
            enableGPSLocations();
        }
    }

    public void onPause(){
        super.onPause();
        accelerometerHandling.onPause();
        stepCounterHandling.onPause();
        heartRateHandling.onPause();
        //TODO: Vogliamo stoppare gli update delle location quando l'app è in pausa o no?
        //fusedLocationClient.removeLocationUpdates(gpsHandling.getLocationCallback());
    }

    @SuppressLint("MissingPermission")
    public void enableGPSLocations(){
        if(locationUpdating == false){
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
        sensorsUpdating = true;
    }
}