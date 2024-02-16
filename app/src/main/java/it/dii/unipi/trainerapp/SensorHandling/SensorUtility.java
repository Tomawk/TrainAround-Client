package it.dii.unipi.trainerapp.SensorHandling;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;

import java.util.List;

public final class SensorUtility {

    static final int sdkVersion = Build.VERSION.SDK_INT;

    static final String[] PERMISSIONS_UNDER_SDK29 = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BODY_SENSORS
    };

    static final String[] PERMISSIONS_OVER_SDK29 = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
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
        if(sdkVersion >= 29){
            if (!hasPermissions(PERMISSIONS_OVER_SDK29,ctx)) {
                Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");

                multiplePermissionLauncher.launch(PERMISSIONS_OVER_SDK29);
            } else {
                Log.d("PERMISSIONS", "All permissions are already granted");
            }
        } else { //sdkVersion <= 28
            if (!hasPermissions(PERMISSIONS_UNDER_SDK29,ctx)) {
                Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");

                multiplePermissionLauncher.launch(PERMISSIONS_UNDER_SDK29);
            } else {
                Log.d("PERMISSIONS", "All permissions are already granted");
            }
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

    public static String[] getPERMISSIONS_UNDER_SDK29() {
        return PERMISSIONS_UNDER_SDK29;
    }

    public static String[] getPERMISSIONS_OVER_SDK29() {
        return PERMISSIONS_OVER_SDK29;
    }

    public static int getSdkVersion() {
        return sdkVersion;
    }



}
