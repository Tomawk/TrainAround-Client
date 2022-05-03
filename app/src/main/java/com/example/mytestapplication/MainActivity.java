package com.example.mytestapplication;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.mytestapplication.SensorHandling.AccelerometerHandling;
import com.example.mytestapplication.SensorHandling.StepCounterHandling;
import com.example.mytestapplication.databinding.ActivityMainBinding;

public class MainActivity extends Activity{

    private static final String TAG = "MainActivity";

    private TextView mTextView;
    private ActivityMainBinding binding;
    private AccelerometerHandling accelerometerHandling;
    private StepCounterHandling stepCounterHandling;
    private int stepcounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    //accelerometerHandling = new AccelerometerHandling((SensorManager)getSystemService(SENSOR_SERVICE), this);
    stepCounterHandling = new StepCounterHandling((SensorManager)getSystemService(SENSOR_SERVICE), this);

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
        stepCounterHandling.onResume();
    }

    public void onPause(){
        super.onPause();
        //accelerometerHandling.onPause();
        stepCounterHandling.onPause();
    }

    public void printAccelValues(float[] values){
        String x_axis = Float.toString(values[0]);
        String y_axis = Float.toString(values[1]);
        String z_axis = Float.toString(values[2]);
        String output_str = "x_axis = " + x_axis + "; y_axis = " + y_axis + "; z_axis = " + z_axis + ";";
        Log.d(TAG,output_str);
    }
    /*
    public void printStepValues(){

        Log.d(TAG,Integer.toString(stepcounter));
    }*/

}