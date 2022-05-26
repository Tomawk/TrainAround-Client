package com.example.mytestapplication.SensorHandling;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mytestapplication.MainActivity;

import java.util.ArrayList;
import java.util.List;

public final class SensorUtility {

    static final String[] PERMISSIONS = {
            //Manifest.permission.ACTIVITY_RECOGNITION, //TODO EMULATOR DOESN'T HAVE STEPCOUNTER SO MUST BE COMMENTED
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BODY_SENSORS
    };


    private static boolean hasPermissions(String[] permissions, Context ctx) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(ctx , permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    public static void askPermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher, Context ctx) {
        if (!hasPermissions(PERMISSIONS,ctx)) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionLauncher.launch(PERMISSIONS);
        } else {
            Log.d("PERMISSIONS", "All permissions are already granted");
        }
    }

    public static String getSensorList(SensorManager sm){
        List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
        String ret_str = "";
        for (Sensor sensor_elem : sensorList) {
            ret_str = ret_str + sensor_elem.toString() + "\n";
        }
        return ret_str;
    }

    public static String[] getPERMISSIONS() {
        return PERMISSIONS;
    }

}
