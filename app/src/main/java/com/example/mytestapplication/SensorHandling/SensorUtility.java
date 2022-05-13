package com.example.mytestapplication.SensorHandling;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mytestapplication.MainActivity;

import java.util.ArrayList;
import java.util.List;

public final class SensorUtility {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public static String getSensorList(SensorManager sm){
        List<Sensor> sensorList = sm.getSensorList(Sensor.TYPE_ALL);
        String ret_str = "";
        for (Sensor sensor_elem : sensorList) {
            ret_str = ret_str + sensor_elem.toString() + "\n";
        }
        return ret_str;
    }

    public static boolean checkAndRequestPermissions(MainActivity mainActivity) {

        int activity_recognition = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACTIVITY_RECOGNITION);
        int location_fine = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        int location_coarse = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int body_sensors = ContextCompat.checkSelfPermission(mainActivity,Manifest.permission.BODY_SENSORS);
        List<String> listPermissionsNeeded = new ArrayList<>();


        if (activity_recognition != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }
        if (location_fine != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (location_coarse != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (body_sensors != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BODY_SENSORS);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(mainActivity,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
