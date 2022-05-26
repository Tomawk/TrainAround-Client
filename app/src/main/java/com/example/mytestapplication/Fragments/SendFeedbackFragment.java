package com.example.mytestapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;

import com.example.mytestapplication.MainActivity;
import com.example.mytestapplication.Others.Preferences;

public class SendFeedbackFragment extends PreferenceFragmentCompat {

    private String feedback_pathfile = "feedbackDump.txt";
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Toast.makeText(getContext(), "Feedback sent correctly", Toast.LENGTH_LONG).show();
        String feedback_str = "_rating:" + Preferences.getFeedbackRating() + " _comment:" + Preferences.getFeedbackComment();
        Log.e("Feedback Sent",feedback_str);
        Intent myIntent = new Intent(getContext(), MainActivity.class);
        startActivity(myIntent);
    }
}