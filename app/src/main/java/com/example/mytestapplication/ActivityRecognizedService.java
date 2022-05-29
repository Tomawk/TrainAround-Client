package com.example.mytestapplication;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognizedService extends IntentService {
    private Context ctx;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for (DetectedActivity activity : probableActivities) {
            switch (activity.getType()) {
                /*case DetectedActivity.IN_VEHICLE: {
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    if (activity.getConfidence() >= 75) {
                        sendMessageToActivity("IN_VEHICLE", DetectedActivity.IN_VEHICLE);
                    }
                    break;
                }*/
                case DetectedActivity.ON_BICYCLE: {
                    Log.e("ActivityRecogition", "On Bicycle: " + activity.getConfidence());
                    if (activity.getConfidence() >= 50) {
                        sendMessageToActivity("ON_BICYCLE", DetectedActivity.ON_BICYCLE);
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e("ActivityRecogition", "Running: " + activity.getConfidence());
                    if (activity.getConfidence() >= 50) {
                        sendMessageToActivity("RUNNING", DetectedActivity.RUNNING);
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e("ActivityRecogition", "Still: " + activity.getConfidence());
                    if (activity.getConfidence() >= 50) {
                        sendMessageToActivity("STILL", DetectedActivity.STILL);
                    }
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e("ActivityRecogition", "Walking: " + activity.getConfidence());
                    if (activity.getConfidence() >= 50) {
                        sendMessageToActivity("WALKING", DetectedActivity.WALKING);
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e("ActivityRecogition", "Unknown: " + activity.getConfidence());
                    if (activity.getConfidence() >= 75) {
                        sendMessageToActivity("UNKNOWN", DetectedActivity.UNKNOWN);
                    }
                    break;
                }
            }
        }
    }

    private void sendMessageToActivity(String activity, int activityInt) {
        Intent intent = new Intent("ActivityRecognized");
        // You can also include some extra data.
        intent.putExtra("Activity", activity);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Request
        Transactions.writeActivity(getApplicationContext(), activity, activityInt);
    }

}