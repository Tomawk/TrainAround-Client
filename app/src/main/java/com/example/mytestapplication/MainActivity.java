package com.example.mytestapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean user_set = false;
    private String athleteName;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private Preferences myPreferences;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPrefListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    //ATHLETE NAME
                    updateAthleteTextView(Preferences.getAtheleteName());

                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPreferences = Preferences.getPreferences(this);
        myPreferences.registerOnSharedPreferenceChangeListener(sharedPrefListener); // register for changes on preferences
/*
        if(!SensorUtility.checkAndRequestPermissions(this)) {
            Log.d(TAG,"Not all permissions have been granted!");
        } else {
            Log.d(TAG,"Permissions have been granted!");
        }*/

        setContentView(R.layout.activity_main);

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

    public void updateDarkTheme(boolean darkThemeEnabled) {
        if (darkThemeEnabled){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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