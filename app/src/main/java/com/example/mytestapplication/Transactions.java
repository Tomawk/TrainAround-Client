package com.example.mytestapplication;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mytestapplication.GATTclient.GATTClientService;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Transactions {

    public static final String TRANSACTION_ACTION = "write-transaction";
    public static final String TRANSACTION_TYPES = "transaction-type";
    public static final String DATA = "data";

    public enum TRANSACTION_TYPE {
        NAME,
        HEART_RATE,
        SPEED,
        STEPS,
        ACTIVITY,
        PACE,
        DISTANCE
    }

    public static void writeAthleteName(Context context, String athleteName){
        broadcastMessage(context, TRANSACTION_TYPE.NAME, athleteName.getBytes(StandardCharsets.UTF_8));
        return;
    }

    public static void writeHeartRate(Context context, int heartRate) {
        BigInteger bigIntHR = BigInteger.valueOf(heartRate);
        broadcastMessage(context, TRANSACTION_TYPE.HEART_RATE, bigIntHR.toByteArray());
        return;
    }

    public static void writeSpeed(Context context, double speed) {
        double speedRounded = Math.round(speed * 100.0) / 100.0;
        byte[] speedBytes = ByteBuffer.allocate(8).putDouble(speedRounded).array();
        broadcastMessage(context, TRANSACTION_TYPE.SPEED, speedBytes);
        return;
    }

    public static void writeSteps(Context context, float steps){
        BigInteger bigIntSteps = BigInteger.valueOf((int) steps);
        broadcastMessage(context, TRANSACTION_TYPE.STEPS, bigIntSteps.toByteArray());
        return;
    }

    public static void writeActivity(Context context, String activityName, int activityInt){
        BigInteger bigIntActivity = BigInteger.valueOf(activityInt);
        broadcastMessage(context, TRANSACTION_TYPE.ACTIVITY, bigIntActivity.toByteArray());
        return;
    }

    public static void writePace(Context context, double pace) {
        double paceRounded = Math.round(pace * 10.0) / 10.0;
        byte[] paceBytes = ByteBuffer.allocate(8).putDouble(paceRounded).array();
        broadcastMessage(context, TRANSACTION_TYPE.PACE, paceBytes);
        return;
    }

    public static void writeDistance(Context context, double distance){
        double distanceRounded = Math.round(distance * 10.0) / 10.0;
        byte[] distanceBytes = ByteBuffer.allocate(8).putDouble(distanceRounded).array();
        broadcastMessage(context, TRANSACTION_TYPE.DISTANCE, distanceBytes);
        return;
    }

    private static void broadcastMessage(Context context, TRANSACTION_TYPE transaction_type, byte[] data){
        final Intent intent = new Intent(TRANSACTION_ACTION);
        intent.putExtra(TRANSACTION_TYPES, transaction_type);
        intent.putExtra(DATA, data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
