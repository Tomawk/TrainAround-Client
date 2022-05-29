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

public class HeartRateHandling implements SensorEventListener {

    private static final String TAG = "HeartRate";

    private final SensorManager mSensorManager;
    private final Sensor mHeartMonitoring;
    private final Context context;

    public HeartRateHandling(SensorManager sm, Context ctx){
        context = ctx;
        mSensorManager = sm;
        mHeartMonitoring = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
    }

    public void onResume(){
        //Registers a SensorEventListener for the given sensor at the given sampling frequency.
    }

    public void onPause(){

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        Transactions.writeHeartRate(context, (int) event.values[0]);
        printHeartMonitoring(event.values);

    }

    public void stopSensor(){
        Log.i(TAG, "HeartRate sensor disabled!");
        mSensorManager.unregisterListener(this, mHeartMonitoring);
    }

    public void enableSensor(){
        Log.i(TAG, "HeartRate sensor enabled!");
        mSensorManager.registerListener(this, mHeartMonitoring, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void printHeartMonitoring(float[] values){
        TextView textView_heart = (TextView) ((Activity)context).findViewById(R.id.textView_heart);;
        textView_heart.setText("Heart Rate: " + values[0]);
        textView_heart.setTextColor(Color.GREEN);
    }
}
