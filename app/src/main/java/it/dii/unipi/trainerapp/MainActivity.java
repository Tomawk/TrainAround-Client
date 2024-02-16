package it.dii.unipi.trainerapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import it.dii.unipi.trainerapp.Others.Preferences;
import it.dii.unipi.trainerapp.GATTclient.GATTClientService;
import it.dii.unipi.trainerapp.SensorHandling.SensorUtility;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // set this field to true if you want to test the app in the emulator
    private static final boolean TESTING_ENV_WITHOUT_BL_ACCESS = false;

    private boolean user_set = false;
    private String athleteName;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private Preferences myPreferences;

    private Button connect_btn;
    private Button disconnect_btn;
    private Button start_activity_btn;
    private Button start_scanning_btn;
    private ImageView settings_btn;

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

            connect_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Going to connect...");
                    bluetoothService.connect();
                }
            });

            disconnect_btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Going to disconnect...");
                    bluetoothService.closeGATTConnection();
                }
            });

            start_scanning_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "start_scanning btn pressed, prompting the GATTClientService to start another scan");
                    bluetoothService.scanLeDevice();
                    // disable the scan_btn while scan is in execution
                    start_scanning_btn.setEnabled(false);
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
                    start_activity_btn.setEnabled(true);
                    Transactions.writeAthleteName(getApplicationContext(), athleteName);
                    Toast.makeText(getApplicationContext(), "connected to server", Toast.LENGTH_LONG).show();
                    connect_btn.setVisibility(View.GONE);
                    disconnect_btn.setVisibility(View.VISIBLE);
                    break;
                case GATT_SERVER_DISCOVERED:
                    start_scanning_btn.setEnabled(false);
                    start_scanning_btn.setVisibility(View.GONE);
                    connect_btn.setVisibility(View.VISIBLE);
                    connect_btn.setEnabled(true);
                    break;
                case GATT_SERVER_DISCONNECTED:
                    Log.i(TAG, "MainActivity: received message of disconnection from GATTServer");
                    Toast.makeText(getApplicationContext(), "Disconnected from server, will retry to connect", Toast.LENGTH_LONG).show();
                    start_activity_btn.setEnabled(false);
                    start_scanning_btn.setVisibility(View.VISIBLE);
                    start_scanning_btn.setEnabled(true);
                    disconnect_btn.setVisibility(View.GONE);
                    break;
                case GATT_SERVER_NOT_FOUND:
                    Log.i(TAG, "No app GATTServer found, should notify the user");
                    Toast.makeText(MainActivity.this, "No server found", Toast.LENGTH_LONG).show();
                    start_scanning_btn.setEnabled(true);
                    start_scanning_btn.setVisibility(View.VISIBLE);
                    break;
                case GATT_SERVER_SCANNING:
                    Log.v(TAG, "the GATTClientService is performing a scan for BLE GATTServer.. disable start_scanning_btn");
                    start_scanning_btn.setEnabled(false);
                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        connect_btn = (Button)findViewById(R.id.connect_btn);
        disconnect_btn = (Button)findViewById(R.id.disconnect_btn);
        start_activity_btn = (Button)findViewById(R.id.start_btn);
        start_scanning_btn = (Button) findViewById(R.id.start_scanning_btn);
        settings_btn = (ImageView) findViewById(R.id.settings_btn);

        if(TESTING_ENV_WITHOUT_BL_ACCESS == true){
            Log.w(TAG, "since this is a testing env without BL access, will enable start activity btn");
            start_activity_btn.setEnabled(true);
            Log.w(TAG, "since this is a testing env without BL access, will hide connect and scan buttons");
            connect_btn.setVisibility(View.GONE);
            connect_btn.setEnabled(false);
            start_scanning_btn.setVisibility(View.GONE);
            start_scanning_btn.setEnabled(false);
        }

        myPreferences = Preferences.getPreferences(this);
        myPreferences.registerOnSharedPreferenceChangeListener(sharedPrefListener); // register for changes on preferences

        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract, isGranted -> {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                if (SensorUtility.getSdkVersion() >= 29){
                    multiplePermissionLauncher.launch(SensorUtility.getPERMISSIONS_OVER_SDK29());
                } else{
                    multiplePermissionLauncher.launch(SensorUtility.getPERMISSIONS_UNDER_SDK29());
                }
            }
        });

        SensorUtility.askPermissions(multiplePermissionLauncher,this);

        /* GATTClient service is bound with the main activity */
        Intent gattServiceIntent = new Intent(this, GATTClientService.class);
        boolean bound = bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        start_activity_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(),SensorActivity.class);
                startActivity(myIntent);
            }
        });

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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(clientReceiver);
        unbindService(serviceConnection);
        super.onDestroy();
    }
}