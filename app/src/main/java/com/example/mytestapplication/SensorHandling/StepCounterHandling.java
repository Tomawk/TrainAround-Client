package com.example.mytestapplication.SensorHandling;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.mytestapplication.MainActivity;

import java.util.List;

public class StepCounterHandling implements SensorEventListener {
    private static final String TAG = "StepcounterHandling";

    private final SensorManager mSensorManager;
    private final Sensor mStepCounter;
    private final MainActivity mainActivity;
    private int stepcounter = 0;

    public StepCounterHandling(SensorManager sm, MainActivity ma){
        // this.context = context;
        mainActivity = ma;
        mSensorManager = sm;
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor_elem : sensorList) {
            Log.d(TAG, sensor_elem.toString());
        }
    }

    public void onResume(){
        Log.d(TAG, "registerSteps: on");
        //Registers a SensorEventListener for the given sensor at the given sampling frequency.
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onPause(){
        Log.d(TAG, "unregisterSteps: off");
        mSensorManager.unregisterListener(this, mStepCounter);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        // Here we call a method in MainActivity and pass it the values from the SensorChanged event
        stepcounter++;
        Log.d(TAG,Integer.toString(stepcounter));

    }
}
