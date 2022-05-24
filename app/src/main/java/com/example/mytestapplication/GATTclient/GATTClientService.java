package com.example.mytestapplication.GATTclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Process;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GATTClientService extends Service {

    private Binder binder = new LocalBinder();
    public static final String TAG = "BluetoothLeService";

    /* Scanning fields */
    private BluetoothDevice serverAddress;
    private BluetoothLeScanner bluetoothLeScanner;
    private boolean scanning;
    private Handler handler = new Handler();

    /* Connection fields */
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    /* needed to manage the thread created by the service */
    private HandlerThread thread;
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // we insert here the functionalities executed by the thread
            initialize();
            scanLeDevice();
        }
    }

    public class LocalBinder extends Binder {
        public GATTClientService getService() {
            return GATTClientService.this;
        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                Log.d(TAG, "Successfully connected to the GATT Server");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                Log.d(TAG, "Disconnected from the GATT Server");
            }
        }
    };

    @Override
    public void onCreate(){

    }

    public boolean initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        } else {
            Log.d(TAG, "BluetoothAdapter successfully obtained.");
        }
        return true;
    }

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    // Device scan callback
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    serverAddress = result.getDevice();

                    // debug logging to understand found devices
                    Log.d(TAG, "Available device: " + result);
                }

                @Override
                public void onScanFailed(int error){
                    Log.d(TAG, "Error on the scan: " + error);
                }
            };

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        UUID ATHLETE_INFORMATION_SERVICE = UUID.fromString("a173b614-8dff-455d-83d1-37de25b9432c");

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Bluetooth scan permission not granted");
            return;
        }*/

        ScanFilter filter = new ScanFilter.Builder()
                //.setServiceUuid(new ParcelUuid(ATHLETE_INFORMATION_SERVICE))
                //.setServiceData(new ParcelUuid(ATHLETE_INFORMATION_SERVICE), "trainer".getBytes(StandardCharsets.UTF_8))
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(filter);

        ScanSettings settings = new ScanSettings.Builder().build();

        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(filters, settings, leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    @SuppressLint("MissingPermission")
    public boolean connect() {
        if (bluetoothAdapter == null || serverAddress == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device;

        try {
            device = bluetoothAdapter.getRemoteDevice(serverAddress.toString());
        } catch (IllegalArgumentException exception) {
            Log.w(TAG, "Device not found with provided address.");
            return false;
        }

        // connect to the GATT server on the device
        Log.d(TAG, "Connecting to " + device.toString());
        bluetoothGatt = device.connectGatt(this, true, bluetoothGattCallback, 2);
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "GATTclient binding");

        thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        Message msg = serviceHandler.obtainMessage();
        serviceHandler.sendMessage(msg);

        return binder;
    }

}