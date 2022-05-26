package com.example.mytestapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mytestapplication.Others.Preferences;
import com.example.mytestapplication.GATTclient.BluetoothUtility;
import com.example.mytestapplication.GATTclient.GATTClientService;
import com.example.mytestapplication.SensorHandling.AccelerometerHandling;
import com.example.mytestapplication.SensorHandling.GPSHandling;
import com.example.mytestapplication.SensorHandling.HeartRateHandling;
import com.example.mytestapplication.SensorHandling.SensorUtility;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean user_set = false;
    private String athleteName;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private Preferences myPreferences;


    private ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;


    private SharedPreferences.OnSharedPreferenceChangeListener sharedPrefListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    //TextView must be updated with the athlete name specified
                    updateAthleteTextView(Preferences.getAtheleteName());

                }
            };

    /* Bluetooth connection fields */
    private GATTClientService bluetoothService;

    /* This implementation of ServiceConnection is used to listen for the connection and disconnection events */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            bluetoothService = ((GATTClientService.LocalBinder) service).getService();
            if (bluetoothService == null) {
                    Log.e(TAG, "Bluetooth GATT Client unavailable!");
                    finish();
            }

            Button button = (Button)findViewById(R.id.connect_btn);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Going to connect...");
                    bluetoothService.connect();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };

    private BroadcastReceiver clientReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            GATTClientService.GATT_UPDATE_TYPES update_type = (GATTClientService.GATT_UPDATE_TYPES) intent.getSerializableExtra(GATTClientService.GATT_UPDATE_TYPE);
            Log.i(TAG, "Update received from the client " + update_type);

            switch (update_type) {
                case GATT_SERVER_CONNECTED:
                    Button startActivityButton = (Button) findViewById(R.id.start_btn);
                    startActivityButton.setEnabled(true);
                    Transactions.writeAthleteName(getApplicationContext(), athleteName);
                    break;
                case GATT_SERVER_DISCOVERED:
                    Button connectBtn = (Button) findViewById(R.id.connect_btn);
                    connectBtn.setEnabled(true);
                    break;
                case GATT_SERVER_DISCONNECTED:
                    break;
            }

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
        myPreferences = Preferences.getPreferences(this);
        myPreferences.registerOnSharedPreferenceChangeListener(sharedPrefListener); // register for changes on preferences

        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract, isGranted -> {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                multiplePermissionLauncher.launch(SensorUtility.getPERMISSIONS());
            }
        });

        SensorUtility.askPermissions(multiplePermissionLauncher,this);

        setContentView(R.layout.activity_main);

        /* GATTClient service is bound with the main activity */
        Intent gattServiceIntent = new Intent(this, GATTClientService.class);
        boolean bound = bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        Button start_activity_btn = (Button)findViewById(R.id.start_btn);
        start_activity_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(),SensorActivity.class);
                startActivity(myIntent);
            }
        });

        ImageView settings_btn = (ImageView) findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(myIntent);
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(clientReceiver,
                new IntentFilter(GATTClientService.GATT_UPDATES_ACTION));
        //firstly checks whether the athlete name has already been set
        athleteName = Preferences.getAtheleteName();

        if(!athleteName.equals("Name not inserted")){
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

    public void updateAthleteTextView(String athleteName){
        TextView athleteNameLabel = (TextView) findViewById(R.id.welcome_textview);
        athleteNameLabel.setText("Welcome, " + athleteName);
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