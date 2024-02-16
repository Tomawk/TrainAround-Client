package it.dii.unipi.trainerapp;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.res.Configuration;
import android.util.Log;

public class AthleteSideApplication extends Application {
    private static final String TAG = AthleteSideApplication.class.getSimpleName();

    private BluetoothDevice GATTServer;

    public BluetoothDevice getGATTServer(){
        if(this.GATTServer == null){
            Log.w(TAG, "trying to access BluetoothDevice class obj GATTServer on empty object!");
            //instead of returning null, consider to raise an intent and wait for the result of the intent requesting a BluetoothDevice instance for GATTServer
            return null;
        }
        return this.GATTServer;
    }

    /**
     * By default this method does not overwrite a previous stored GATTServer (if any)
     * @param GATTServer
     * @return true if the given BluetoothDevice obj was stored, else false
     */
    public boolean setGATTServer(BluetoothDevice GATTServer){
        return this.setGATTServer(GATTServer, false);
    }

    public boolean setGATTServer(BluetoothDevice GATTServer, boolean forceOverwrite){
        if(this.GATTServer != null){
            if(this.GATTServer.equals(GATTServer)){
                Log.w(TAG, "trying to store the same GATTServer object twice");
                return true;
            }
            if(forceOverwrite == false) {
                Log.e(TAG, "A GATTServer obj was already set, cannot overwrite!");
                return false;
            }
            else{
                Log.w(TAG, "going to overwrite the current GATTServer instance whose MAC addr is: '" + this.GATTServer.getAddress() + "' with the new one: '" + GATTServer.getAddress() + "'");
            }
        }
        this.GATTServer = GATTServer;
        return true;
    }

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}