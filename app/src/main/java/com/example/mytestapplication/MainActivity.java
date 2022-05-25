package com.example.mytestapplication;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
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

import com.example.mytestapplication.GATTclient.BluetoothUtility;
import com.example.mytestapplication.GATTclient.GATTClientService;
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

    /* Bluetooth connection fields */
    private GATTClientService bluetoothService;

    /* This implementation of ServiceConnection is used to listen for the connection and disconnection events */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            bluetoothService = ((GATTClientService.LocalBinder) service).getService();
            if (bluetoothService != null) {
                if (!bluetoothService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    finish();
                }

                // after the service connection the button is bound with the onclick function

                Button button = (Button)findViewById(R.id.connect_btn);
                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Going to connect...");
                        bluetoothService.connect();
                    }
                });
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!SensorUtility.checkAndRequestPermissions(this)) {
            Log.d(TAG,"Not all sensors permissions have been granted!");
        } else {
            Log.d(TAG,"Sensor permissions have been granted!");
        }

        if(!BluetoothUtility.checkAndRequestBluetoothPermissions(this)) {
            Log.d(TAG,"Not all Bluetooth permissions have been granted!");
        } else {
            Log.d(TAG,"Bluetooth permissions have been granted!");
        }

        setContentView(R.layout.activity_main);

        /* GATTClient service is bound with the main activity */
        Intent gattServiceIntent = new Intent(this, GATTClientService.class);
        boolean bound = bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

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

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy method called, going to unbind from GATTClientService");
        //this.stopService(new Intent(getApplicationContext(), GATTClientService.class));
        //bluetoothService.stopService();
        //bluetoothService = null;
        unbindService(serviceConnection);
        super.onDestroy();
    }
}