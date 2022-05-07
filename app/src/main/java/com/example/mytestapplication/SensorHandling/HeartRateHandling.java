package com.example.mytestapplication.SensorHandling;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.mytestapplication.MainActivity;

import java.util.List;

public class HeartRateHandling implements SensorEventListener {

    private static final String TAG = "HeartBeat";

    private final SensorManager mSensorManager;
    private final Sensor mHeartMonitoring;
    private final MainActivity mainActivity;

    public HeartRateHandling(SensorManager sm, MainActivity ma){
        // this.context = context;
        mainActivity = ma;
        mSensorManager = sm;
        mHeartMonitoring = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    public void onResume(){
        Log.d(TAG, "registerHeart: on");
        //Registers a SensorEventListener for the given sensor at the given sampling frequency.
        mSensorManager.registerListener(this, mHeartMonitoring, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onPause(){
        Log.d(TAG, "unregisterHeart: off");
        mSensorManager.unregisterListener(this, mHeartMonitoring);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        // Here we call a method in MainActivity and pass it the values from the SensorChanged event
        mainActivity.printHeartMonitoring(event.values);

    }
}
