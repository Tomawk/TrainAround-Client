package com.example.mytestapplication;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.mytestapplication.Fragments.SettingsFragment;

public class SettingsActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
    }
}