package com.example.mytestapplication.GATTclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mytestapplication.MainActivity;

import java.util.ArrayList;
import java.util.List;

public final class BluetoothUtility {

    public static final String TAG = "BluetoothUtility";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public static boolean checkAndRequestBluetoothPermissions(MainActivity mainActivity){

        Log.d(TAG, "Checking bluetooth permission");

        int bluetooth_scanning = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.BLUETOOTH_SCAN);
        int bluetooth_connect = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.BLUETOOTH_CONNECT);
        //int background_location = ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        /*if (background_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }*/
        if (bluetooth_scanning != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
        }
        if (bluetooth_connect != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            Log.d(TAG, "Requesting bluetooth permission ");
            ActivityCompat.requestPermissions(mainActivity,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
