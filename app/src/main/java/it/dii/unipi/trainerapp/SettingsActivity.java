package it.dii.unipi.trainerapp;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import it.dii.unipi.trainerapp.Fragments.SettingsFragment;

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