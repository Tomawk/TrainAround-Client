package com.example.mytestapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mytestapplication.SensorHandling.AccelerometerHandling;
import com.example.mytestapplication.SensorHandling.GPSHandling;
import com.example.mytestapplication.SensorHandling.HeartRateHandling;
import com.example.mytestapplication.SensorHandling.SensorUtility;
import com.example.mytestapplication.SensorHandling.StepCounterHandling;
import com.example.mytestapplication.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity{

    private static final String TAG = "MainActivity";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private TextView mTextView;
    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private GPSHandling gpsHandling;
    private AccelerometerHandling accelerometerHandling;
    //private StepCounterHandling stepCounterHandling;
    private HeartRateHandling heartRateHandling;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(!SensorUtility.checkAndRequestPermissions(this)) {
        Log.d(TAG,"Not all permissions have been granted!");
    } else {
        Log.d(TAG,"Permissions have been granted!");
    }

    gpsHandling = new GPSHandling(this);

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //Permissions already Checked using previous method
        return;
    }

    fusedLocationClient.requestLocationUpdates(gpsHandling.getLocationRequest(),gpsHandling.getLocationCallback(), Looper.getMainLooper());

    //TODO: Non sono sicuro se basti un sensor manager unico o se servano piu sensor manager per ogni sensore (DA VEDERE)
    SensorManager sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

    //Display all the available sensors on the current tested device, debug purpose
    String available_sensor_list = SensorUtility.getSensorList(sensorManager);
    Log.d("SensorUtility",available_sensor_list);

    //Sensor classes instantiations
    accelerometerHandling = new AccelerometerHandling(sensorManager, this);
    //stepCounterHandling = new StepCounterHandling(sensorManager, this);
    heartRateHandling = new HeartRateHandling(sensorManager,this);

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    Button button = (Button)findViewById(R.id.btn_1);
    button.setFocusable(true);
    button.setFocusableInTouchMode(true);
    button.requestFocus();

    mTextView = binding.text;

    }

    public void onResume(){
        super.onResume();
        accelerometerHandling.onResume();
        //stepCounterHandling.onResume();
        heartRateHandling.onResume();
    }

    public void onPause(){
        super.onPause();
        accelerometerHandling.onPause();
        //stepCounterHandling.onPause();
        heartRateHandling.onPause();
    }

    public void printAccelValues(float[] values){
        String x_axis = Float.toString(values[0]);
        String y_axis = Float.toString(values[1]);
        String z_axis = Float.toString(values[2]);
        String output_str = "x_axis = " + x_axis + "; y_axis = " + y_axis + "; z_axis = " + z_axis + ";";
        TextView textView_print = (TextView) findViewById(R.id.textView_print);
        textView_print.setText("Accelerometer: " + output_str);
    }

    public void printStepValues(float[] values){
        TextView textView_print = (TextView) findViewById(R.id.textView_print);
        textView_print.setText("Step Counter: " + Float.toString(values[0]));
    }

    public void printHeartMonitoring(float[] values){
        TextView textView_print2 = (TextView) findViewById(R.id.textView_print2);
        textView_print2.setText("Heart Rate: " + Float.toString(values[0]));
    }

    public void printLocation(double latitude,double longitude,float speed){
        TextView textView_print = (TextView) findViewById(R.id.textView_gps);
        textView_print.setText("Lat: " + latitude +"  " +
                "Lon: " + longitude + "  " +
                "Speed: " + speed);
    }

}