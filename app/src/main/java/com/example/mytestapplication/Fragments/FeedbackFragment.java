package com.example.mytestapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.mytestapplication.R;

public class FeedbackFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.feedback_preferences, rootKey);

    }

}