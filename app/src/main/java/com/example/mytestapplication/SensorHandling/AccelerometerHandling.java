package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.example.mytestapplication.MainActivity;


public class AccelerometerHandling implements SensorEventListener {

    private static final String TAG = "AccelerometerHandling";

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final MainActivity mainActivity;

    public AccelerometerHandling(SensorManager sm, MainActivity ma){
       // this.context = context;
        mainActivity = ma;
        mSensorManager = sm;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /*
    As a best practice for using sensors, it is recommended to unregister the listener
    when the sketch's activity is paused to reduce battery usage, and then registering
    it again when the activity resumes.
     */

    public void onResume(){
        if(mSensorManager != null){
            Log.d(TAG, "registerAccel: on");
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause(){
        if(mSensorManager != null){
            Log.d(TAG, "unregisterAccel: off");
            mSensorManager.unregisterListener(this, mAccelerometer);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        // Here we call a method in MainActivity and pass it the values from the SensorChanged event
        mainActivity.printAccelValues( event.values );
    }
}
