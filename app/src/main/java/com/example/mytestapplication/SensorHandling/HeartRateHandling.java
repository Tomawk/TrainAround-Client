package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.R;

import java.util.List;

public class HeartRateHandling implements SensorEventListener {

    private static final String TAG = "HeartBeat";

    private final SensorManager mSensorManager;
    private final Sensor mHeartMonitoring;
    private final Context context;

    public HeartRateHandling(SensorManager sm, Context ctx){
        // this.context = context;
        context = ctx;
        mSensorManager = sm;
        mHeartMonitoring = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    /*
    As a best practice for using sensors, it is recommended to unregister the listener
    when the sketch's activity is paused to reduce battery usage, and then registering
    it again when the activity resumes.
     */

    public void onResume(){
        //Registers a SensorEventListener for the given sensor at the given sampling frequency.
        if(mSensorManager != null){
            Log.d(TAG, "registerHeart: on");
            mSensorManager.registerListener(this, mHeartMonitoring, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void onPause(){
        if(mSensorManager != null){
            Log.d(TAG, "unregisterHeart: off");
            mSensorManager.unregisterListener(this, mHeartMonitoring);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        // Here we call a method in MainActivity and pass it the values from the SensorChanged event
        printHeartMonitoring(event.values);

    }

    public void printHeartMonitoring(float[] values){
        TextView textView_print2 = (TextView) ((Activity)context).findViewById(R.id.textView_heart);;
        textView_print2.setText("Heart Rate: " + values[0]);
    }
}
