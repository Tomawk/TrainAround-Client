package com.example.mytestapplication;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class AthleteSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.athlete_preferences, rootKey);
    }
}
