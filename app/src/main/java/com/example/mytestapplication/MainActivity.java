package com.example.mytestapplication;

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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mytestapplication.SensorHandling.AccelerometerHandling;
import com.example.mytestapplication.SensorHandling.GPSHandling;
import com.example.mytestapplication.SensorHandling.HeartRateHandling;
import com.example.mytestapplication.SensorHandling.SensorUtility;
import com.example.mytestapplication.SensorHandling.StepCounterHandling;
import com.example.mytestapplication.databinding.ActivityMainBinding;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(!SensorUtility.checkAndRequestPermissions(this)) {
        Log.d(TAG,"Not all permissions have been granted!");
    } else {
        Log.d(TAG,"Permissions have been granted!");
    }

    setContentView(R.layout.activity_main);

    Button button = (Button)findViewById(R.id.start_btn);
    button.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent myIntent = new Intent(getApplicationContext(),SensorActivity.class);
            startActivity(myIntent);
        }
    });

    }

    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
    }

}