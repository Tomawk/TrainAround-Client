package com.example.mytestapplication.GATTclient;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.aware.Characteristics;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Process;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mytestapplication.R;
import com.example.mytestapplication.Transactions;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GATTClientService extends Service {

    private Binder binder = new LocalBinder();
    public static final String TAG = "BluetoothLeService";

    private static UUID ATHLETE_INFORMATION_SERVICE = UUID.fromString("a173b614-8dff-455d-83d1-37de25b9432c");
    public static UUID ATHLETE_NAME_CHARACTERISTIC = UUID.fromString("4fe10359-2ce1-4e3e-848d-aec36a32930c");

    public static UUID HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
    public static UUID HEART_RATE_CHARACTERISTIC = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    public static UUID MOVEMENT_SERVICE = UUID.fromString("6b94f55a-dc3f-11ec-9d64-0242ac120002");
    public static UUID RECOGNIZED_ACTIVITY_CHARACTERISTIC = UUID.fromString("6b94f7e4-dc3f-11ec-9d64-0242ac120002");
    public static UUID SPEED_CHARACTERISTIC = UUID.fromString("6b94f92e-dc3f-11ec-9d64-0242ac120002");
    public static UUID PEACE_CHARACTERISTIC = UUID.fromString("6b94fc58-dc3f-11ec-9d64-0242ac120002");
    public static UUID STEP_COUNTER_CHARACTERISTIC = UUID.fromString("6b94fd70-dc3f-11ec-9d64-0242ac120002");

    private static List<UUID> NEEDED_SERVICES = new ArrayList<UUID>(Arrays.asList(ATHLETE_INFORMATION_SERVICE, HEART_RATE_SERVICE, MOVEMENT_SERVICE));

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
            if( initialize() == true) {
                scanLeDevice();
            }else{
                Log.w(TAG, "could not initialize GATTClient");
            }
            Log.d(TAG, "handleMessage on thread: " + Process.getThreadPriority(Process.myTid()));
        }
    }

    public class LocalBinder extends Binder {
        public GATTClientService getService() {
            return GATTClientService.this;
        }
    }

    private BroadcastReceiver transactionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Transactions.TRANSACTION_TYPE transaction_type = (Transactions.TRANSACTION_TYPE) intent.getSerializableExtra(Transactions.TRANSACTION_TYPES);
            Log.i(TAG, "Transaction received: " + transaction_type);

            switch (transaction_type) {
                case NAME:
                    byte[] athleteName = intent.getByteArrayExtra(Transactions.DATA);
                    writeCharacteristic(ATHLETE_INFORMATION_SERVICE, ATHLETE_NAME_CHARACTERISTIC, athleteName);
                    break;
                case HEART_RATE:
                    byte[] heartRateValue = intent.getByteArrayExtra(Transactions.DATA);
                    writeCharacteristic(HEART_RATE_SERVICE, HEART_RATE_CHARACTERISTIC, heartRateValue);
                    break;
                case SPEED:
                    byte[] speedValue = intent.getByteArrayExtra(Transactions.DATA);
                    writeCharacteristic(MOVEMENT_SERVICE, SPEED_CHARACTERISTIC, speedValue);
                    break;
            }
        }
    };

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                Log.d(TAG, "Successfully connected to the GATT Server");
                discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                Log.d(TAG, "Disconnected from the GATT Server");
                broadcastUpdate(GATT_UPDATE_TYPES.GATT_SERVER_DISCONNECTED);
            }else{
                Log.v(TAG, "gatt: " + gatt + " | status: " + status + " | newState: " + newState);
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.v(TAG, "onServicesDiscovered found new service!");
                Log.v(TAG, "available services: " );

                List<UUID> foundServicesUUIDs = new ArrayList<UUID>();
                for (BluetoothGattService service : gatt.getServices()
                     ) {
                    Log.v(TAG, "service UUID: " + service.getUuid() + " | service.getType(): " + service.getType());
                    foundServicesUUIDs.add(service.getUuid());
                }
                if(foundServicesUUIDs.containsAll(NEEDED_SERVICES)){
                    Log.v(TAG, "all required services were found! | broadcasting new GATT state connected!");

                    broadcastUpdate(GATT_UPDATE_TYPES.GATT_SERVER_CONNECTED);
                }
                else{
                    Log.e(TAG, "not all required services were found...");
                    //this happens because of a caching strategy of anroid BLE library implementation
                    // https://issuetracker.google.com/issues/37012916
                    // https://stackoverflow.com/questions/50739085/how-to-refresh-services-clear-cache
                    // the suggested solution is to force cache evict
                    //
                    // I noticed that if I call gatt.discoverServices(); inside the callback onServiceChanged()
                    // the device finds the "new" services, the problem is that onServiceChanged get fired rarely

                    for (UUID serviceUUID: NEEDED_SERVICES
                    ) {
                        if( ! foundServicesUUIDs.contains(serviceUUID)){
                            Log.v(TAG, "the service " + serviceUUID + " was not found on the server");
                        }
                    }
                    Log.i(TAG, "going to force refresh the available services");
                    forceServiceDiscovery(gatt);
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        private int forcedDiscoveryCounter = 0;
        private final int forcedDiscoveryThreshold = 1;
        /**
         * use this method to force the refresh of available services of a GATTServer
         * @param gatt
         */
        @SuppressLint("MissingPermission")
        private void forceServiceDiscovery(BluetoothGatt gatt){
            if(forcedDiscoveryCounter == forcedDiscoveryThreshold){
                Log.w(TAG, "will not force discovery again since already done " + forcedDiscoveryCounter + " times, threshold limit reached");
                return;
            }
            try {
                // BluetoothGatt gatt
                final Method refresh = gatt.getClass().getMethod("refresh");
                if (refresh != null) {
                    refresh.invoke(gatt);
                    gatt.discoverServices();
                    forcedDiscoveryCounter++;
                    Log.i(TAG, "Forced the refresh of the available services");
                }
            } catch (Exception e) {
                Log.w(TAG, "an exception occurred trying to call gatt.refresh() hidden method : " + e.toString());
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServiceChanged(@NonNull BluetoothGatt gatt) {
            super.onServiceChanged(gatt);
            Log.v(TAG, "received a call to onServiceChanged: should re-discover the services with gatt.discoverServices()");
            gatt.discoverServices();
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
                @SuppressLint("MissingPermission")
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    serverAddress = result.getDevice();

                    // debug logging to understand found devices
                    Log.d(TAG, "Available device: " + result);
                    //Log.d(TAG, "scanRecord.getServiceUuids(): " + result.getScanRecord().getServiceUuids());
                    if(useAsServer(result)){
                        if(scanning){
                            bluetoothLeScanner.stopScan(leScanCallback);
                            scanning = false;
                        }
                    }
                }

                @Override
                public void onScanFailed(int error){
                    Log.d(TAG, "Error on the scan: " + error);
                }
            };

    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        if(bluetoothAdapter == null){
            Log.e(TAG, "Cannot start scanLeDevice since there is no bluetoothAdapter");
            return;
        }
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Bluetooth scan permission not granted");
            return;
        }*/

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(ATHLETE_INFORMATION_SERVICE))
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
                    if(scanning) {
                        Log.v(TAG, "stopping BL scan because of timeout");
                        scanning = false;
                        bluetoothLeScanner.stopScan(leScanCallback);
                        if( isConnectedToGATTServer() ){
                            Log.v(TAG, "no more actions needed since I am already connected to a GATTserver");
                        }else{
                            Log.w(TAG, "scan terminated and no GATT server found, broadcasting new STATUS GATT_SERVER_NOT_FOUND");
                            broadcastUpdate(GATT_UPDATE_TYPES.GATT_SERVER_NOT_FOUND);
                        }
                    }
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(filters, settings, leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    /**
     * use this method to set the new server
     * @param scanResult
     * @return
     */
    @SuppressLint("MissingPermission")
    private boolean useAsServer(ScanResult scanResult){
        BluetoothDevice device = scanResult.getDevice();
        List<ParcelUuid> uuids = scanResult.getScanRecord().getServiceUuids();
        if( ! uuids.contains( new ParcelUuid(ATHLETE_INFORMATION_SERVICE) )){
            Log.d(TAG, "the device '" + device.toString() + "' does not contain ATHLETE_INFORMATION_SERVICE UUID... Cannot use as server");
            Log.v(TAG, "available uuids: " + uuids + "ATHLETE_INFORMATION_SERVICE Uuid: " + ATHLETE_INFORMATION_SERVICE);
            return false;
        }
        Log.i(TAG, "going to use the device '" + device + "' as server");
        this.serverAddress = device;
        broadcastUpdate(GATT_UPDATE_TYPES.GATT_SERVER_DISCOVERED);

        return true;
    }

    /**
     * method to connect to the GATT server, using the address retrieved by the scanning
     * @return
     */
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
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
        //Log.d(TAG, "device.connectGatt returned: " + bluetoothGatt.toString());
        //Log.d(TAG, "bluetoothGatt.connect(): " + bluetoothGatt.connect());
        //Log.d(TAG, "bluetoothGatt.discoverServices(): " + bluetoothGatt.discoverServices());
        //Log.d(TAG, "bluetoothGatt.getServices(): " + bluetoothGatt.getServices().toString());
        return true;
    }

    /**
     * use this to check if the GATTClient is connected to BLE GATT server
     * @return
     */
    private boolean isConnectedToGATTServer(){
        if(bluetoothGatt == null){
            return false;
        }
        return true;
    }

    /**
     *
     */
    @SuppressLint("MissingPermission")
    public void discoverServices(){
        if( isConnectedToGATTServer() == false){
            Log.w(TAG, "cannot discoverServices since I am not connected to a GATTServer");
            return;
        }
        bluetoothGatt.discoverServices();
        return;
    }

    /**
     * method to write a characteristic to the server
     * @param serviceUuid
     * @param characteristicUuid
     * @param value to write on the server
     * @return
     */
    public boolean writeCharacteristic(UUID serviceUuid, UUID characteristicUuid, byte[] value){

        //check bluetoothGatt is available
        if (bluetoothGatt == null) {
            Log.e(TAG, "writeCharacteristic(): lost GATT server connection!");
            return false;
        }
        BluetoothGattService service = bluetoothGatt.getService(serviceUuid);
        if (service == null) {
            Log.e(TAG, "writeCharacteristic(): service not available!");
            return false;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
        if (characteristic == null) {
            Log.e(TAG, "writeCharacteristic(): characteristic not found!");
            return false;
        }

        characteristic.setValue(value);

        @SuppressLint("MissingPermission")
        boolean status = bluetoothGatt.writeCharacteristic(characteristic);
        return status;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "GATTclient binding");

        LocalBroadcastManager.getInstance(this).registerReceiver(transactionReceiver,
                new IntentFilter(Transactions.TRANSACTION_ACTION));

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

    @SuppressLint("MissingPermission")
    @Override
    public boolean onUnbind(Intent intent) {

        Log.d(TAG, "onUbind has been called, going to call service.stopSelf");

        stopSelf();

        return super.onUnbind(intent);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {

        Log.d(TAG, "onDestroy has been called, going to stop BLE Scan and service thread");

        if(scanning){
            bluetoothLeScanner.stopScan(leScanCallback);
        }
        if(bluetoothGatt != null){
            Log.v(TAG, "Disconnecting from server");
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        thread.quit();
        super.onDestroy();
    }

    public enum GATT_UPDATE_TYPES{
        GATT_SERVER_DISCOVERED,
        GATT_SERVER_CONNECTED,
        GATT_SERVER_DISCONNECTED,
        GATT_SERVER_NOT_FOUND
    }

    public final static String GATT_UPDATES_ACTION = "gatt-updates";
    public final static String GATT_UPDATE_TYPE = "gatt-update-type";

    /**
     * call this method to notify the rest of the app for GATT connection changes
     * @param updateType
     */
    private void broadcastUpdate(final GATT_UPDATE_TYPES updateType) {
        final Intent intent = new Intent(GATT_UPDATES_ACTION);
        intent.putExtra(GATT_UPDATE_TYPE, updateType);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}