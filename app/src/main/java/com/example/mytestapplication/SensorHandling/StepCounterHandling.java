package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.R;
import com.example.mytestapplication.SensorActivity;
import com.example.mytestapplication.Transactions;

import java.util.List;

public class StepCounterHandling implements SensorEventListener {
    private static final String TAG = "StepcounterHandling";

    private final SensorManager mSensorManager;
    private final Sensor mStepCounter;
    private final Context context;
    private float initialSteps = 0;
    private float stepsAtm = 0;
    private float stepsAtStill = 0;

/*
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stepsAtStill = stepsAtm;
        }
    };
*/
    public StepCounterHandling(SensorManager sm, Context ctx){
        context = ctx;
        mSensorManager = sm;
        mStepCounter = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
/*
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver,new IntentFilter("StillnessAlert"));
*/

    }

    public void onResume(){

    }

    public void onPause(){

    }

    public void onDestroy(){
       // LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if(initialSteps == 0){
            initialSteps = event.values[0];
            printStepValues(0);
        } else {
            float new_steps = event.values[0] - initialSteps;
            stepsAtm = new_steps;
            if(new_steps >= (stepsAtStill+50)){
                //GPS Sensor should be resumed
                sendResumingAlert();
            }
            printStepValues(new_steps);
            Transactions.writeSteps(context.getApplicationContext(), new_steps);
        }
    }

    public void stopSensor(){
        Log.i(TAG, "StepCounter sensor disabled!");
        mSensorManager.unregisterListener(this, mStepCounter);
    }

    public void enableSensor(){
        //Registers a SensorEventListener for the given sensor at the given sampling frequency.
        Log.i(TAG, "StepCounter sensor enabled!");
        mSensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void printStepValues(float new_steps){
        TextView textView_steps = (TextView) ((Activity)context).findViewById(R.id.textView_steps);
        textView_steps.setText("Step Counter: " + new_steps);
        textView_steps.setTextColor(Color.GREEN);
    }

    public void setStepsAtStill(){
        stepsAtStill = stepsAtm;
    }

    private void sendResumingAlert() {
        Intent intent = new Intent("ResumeGPS");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
