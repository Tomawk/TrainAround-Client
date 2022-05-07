package com.example.mytestapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mytestapplication.SensorHandling.HeartRateHandling;
import com.example.mytestapplication.SensorHandling.StepCounterHandling;
import com.example.mytestapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{

    private static final String TAG = "MainActivity";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private TextView mTextView;
    private ActivityMainBinding binding;
    //private AccelerometerHandling accelerometerHandling;
    //private StepCounterHandling stepCounterHandling;
    private HeartRateHandling heartRateHandling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(!checkAndRequestPermissions()) Log.d(TAG,"Not all permissions have been granted!");
        else Log.d(TAG,"Permissions have been granted!");

    //accelerometerHandling = new AccelerometerHandling((SensorManager)getSystemService(SENSOR_SERVICE), this);
    SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    //stepCounterHandling = new StepCounterHandling(sensorManager, this);
    heartRateHandling = new HeartRateHandling(sensorManager,this);

     binding = ActivityMainBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

     Button button = (Button)findViewById(R.id.btn_1);
     button.setFocusable(true);
     button.setFocusableInTouchMode(true);
     button.requestFocus();

     Button button2 = (Button)findViewById(R.id.btn_2);
     button2.setFocusable(true);
     button2.setFocusableInTouchMode(true);
     button2.requestFocus();

     mTextView = binding.text;
    }

    public void onResume(){
        super.onResume();
       // accelerometerHandling.onResume();
        //stepCounterHandling.onResume();
        heartRateHandling.onResume();
    }

    public void onPause(){
        super.onPause();
        //accelerometerHandling.onPause();
        //stepCounterHandling.onPause();
        heartRateHandling.onPause();
    }

    private  boolean checkAndRequestPermissions() {
        //int wake_lock = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        int body_sensors = ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS);
        int activity_recognition = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);
        List<String> listPermissionsNeeded = new ArrayList<>();
/*
        if (wake_lock != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WAKE_LOCK);
        }*/
        if (body_sensors != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BODY_SENSORS);
        }
        if (activity_recognition != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void printAccelValues(float[] values){
        String x_axis = Float.toString(values[0]);
        String y_axis = Float.toString(values[1]);
        String z_axis = Float.toString(values[2]);
        String output_str = "x_axis = " + x_axis + "; y_axis = " + y_axis + "; z_axis = " + z_axis + ";";
        Log.d(TAG,output_str);
    }

    public void printStepValues(float[] values){
        TextView textView_print = (TextView) findViewById(R.id.textView_print);
        textView_print.setText("Step Counter: " + Float.toString(values[0]));
    }

    public void printHeartMonitoring(float[] values){
        TextView textView_print2 = (TextView) findViewById(R.id.textView_print2);
        textView_print2.setText("Heart Rate: " + Float.toString(values[0]));
    }

}