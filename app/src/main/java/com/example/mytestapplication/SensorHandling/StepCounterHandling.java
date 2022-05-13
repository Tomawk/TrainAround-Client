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

    public StepCounterHandling(SensorManager sm, MainActivity ma){
        // this.context = context;
        mainActivity = ma;
        mSensorManager = sm;
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor_elem : sensorList) {
            Log.d(TAG, sensor_elem.toString());
        }
    }

    /*
    As a best practice for using sensors, it is recommended to unregister the listener
    when the sketch's activity is paused to reduce battery usage, and then registering
    it again when the activity resumes.
     */

    public void onResume(){
        //Registers a SensorEventListener for the given sensor at the given sampling frequency.
        if(mSensorManager != null){
            Log.d(TAG, "registerSteps: on");
            mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void onPause(){
        if(mSensorManager != null){
            Log.d(TAG, "unregisterSteps: off");
            mSensorManager.unregisterListener(this, mStepCounter);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        // Here we call a method in MainActivity and pass it the values from the SensorChanged event
        mainActivity.printStepValues(event.values);

    }
}
