package com.example.mytestapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Activity");
            TextView textView_print = (TextView) findViewById(R.id.textView_activity);
            textView_print.setText("Activity Recognized: " + message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        gpsHandling = new GPSHandling(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permissions already Checked using previous method
            return;
        }

        fusedLocationClient.requestLocationUpdates(gpsHandling.getLocationRequest(),gpsHandling.getLocationCallback(), Looper.getMainLooper());

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
    }

    public void onPause(){
        super.onPause();
        accelerometerHandling.onPause();
        stepCounterHandling.onPause();
        heartRateHandling.onPause();
    }
}