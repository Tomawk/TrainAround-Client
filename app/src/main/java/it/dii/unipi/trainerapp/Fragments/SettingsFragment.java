package it.dii.unipi.trainerapp.Fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import it.dii.unipi.trainerapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);
    }
}