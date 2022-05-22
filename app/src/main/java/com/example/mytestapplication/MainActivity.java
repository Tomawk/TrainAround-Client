package com.example.mytestapplication;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mytestapplication.SensorHandling.AccelerometerHandling;
import com.example.mytestapplication.SensorHandling.GPSHandling;
import com.example.mytestapplication.SensorHandling.HeartRateHandling;
import com.example.mytestapplication.SensorHandling.SensorUtility;
import com.example.mytestapplication.SensorHandling.StepCounterHandling;
import com.example.mytestapplication.databinding.ActivityMainBinding;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ComponentActivity {

    private static final String TAG = "MainActivity";
    private boolean user_set = false;
    private String athleteName;
    private String settings_filename = "nameDump.txt";
    private ActivityResultLauncher<Intent> someActivityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if(!SensorUtility.checkAndRequestPermissions(this)) {
        Log.d(TAG,"Not all permissions have been granted!");
    } else {
        Log.d(TAG,"Permissions have been granted!");
    }

    setContentView(R.layout.activity_main);

    Button button = (Button)findViewById(R.id.start_btn);
    button.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent myIntent = new Intent(getApplicationContext(),SensorActivity.class);
            startActivity(myIntent);
        }
    });


    athleteName = Utility.readFromFile(this, settings_filename);

    if(!athleteName.equals("")){
        user_set=true;
    }

    if(!user_set){
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        athleteName = data.getStringExtra("athleteName");
                        user_set = true;
                        Toast.makeText(getApplicationContext(),"Settings saved successfully!", Toast.LENGTH_SHORT).show();
                        TextView athleteNameLabel = (TextView) findViewById(R.id.welcome_textview);
                        athleteNameLabel.append(athleteName);
                    }
                });
        //send an intent to Settings Activity
        openSomeActivityForResult();
    } else{
        TextView trainerNameLabel = (TextView) findViewById(R.id.welcome_textview);
        trainerNameLabel.append(athleteName);
    }

    }

    public void openSomeActivityForResult() {
        Intent intent = new Intent(this, NamePopUpActivity.class);
        someActivityResultLauncher.launch(intent);
    }

    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
    }

}