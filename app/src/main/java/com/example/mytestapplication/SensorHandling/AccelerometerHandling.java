package com.example.mytestapplication.SensorHandling;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.R;


public class AccelerometerHandling implements SensorEventListener {

    private static final String TAG = "AccelerometerHandling";

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Context context;

    public AccelerometerHandling(SensorManager sm, Context ctx){
       // this.context = context;
        context = ctx;
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
        printAccelValues( event.values );
    }

    public void printAccelValues(float[] values){
        String x_axis = Float.toString(values[0]);
        String y_axis = Float.toString(values[1]);
        String z_axis = Float.toString(values[2]);
        String output_str = "x_axis = " + x_axis + "; y_axis = " + y_axis + "; z_axis = " + z_axis + ";";
        TextView textView_print = (TextView) ((Activity)context).findViewById(R.id.textView_accl);
        textView_print.setText("Accelerometer: " + output_str);
    }
}
