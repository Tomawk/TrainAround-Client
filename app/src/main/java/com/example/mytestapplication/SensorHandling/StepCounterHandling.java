package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.R;
import com.example.mytestapplication.Transactions;

import java.util.List;

public class StepCounterHandling implements SensorEventListener {
    private static final String TAG = "StepcounterHandling";

    private final SensorManager mSensorManager;
    private final Sensor mStepCounter;
    private final Context context;
    private float initialSteps = 0;

    public StepCounterHandling(SensorManager sm, Context ctx){
        context = ctx;
        mSensorManager = sm;
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    /*
    As a best practice for using sensors, it is recommended to unregister the listener
    when the sketch's activity is paused to reduce battery usage, and then registering
    it again when the activity resumes.
     */

    public void onResume(){
        //Registers a SensorEventListener for the given sensor at the given sampling frequency.
        Log.i(TAG, "StepCounter sensor enabled!");
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void onPause(){
        Log.i(TAG, "StepCounter sensor disabled!");
        mSensorManager.unregisterListener(this, mStepCounter);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if(initialSteps == 0){
            initialSteps = event.values[0];
            printStepValues(0);
        } else {
            float new_steps = event.values[0] - initialSteps;
            printStepValues(new_steps);
            Transactions.writeSteps(context.getApplicationContext(), new_steps);
        }
    }

    public void printStepValues(float new_steps){
        TextView textView_steps = (TextView) ((Activity)context).findViewById(R.id.textView_steps);
        textView_steps.setText("Step Counter: " + new_steps);
        textView_steps.setTextColor(Color.GREEN);
    }
}
