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

public class StepCounterHandling implements SensorEventListener {
    private static final String TAG = "StepcounterHandling";

    private final SensorManager mSensorManager;
    private final Sensor mStepCounter;
    private final Context context;

    public StepCounterHandling(SensorManager sm, Context ctx){
        context = ctx;
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
        printStepValues(event.values);
    }

    public void printStepValues(float[] values){
        TextView textView_print = (TextView) ((Activity)context).findViewById(R.id.textView_print3);
        textView_print.setText("Step Counter: " + values[0]);
    }
}
