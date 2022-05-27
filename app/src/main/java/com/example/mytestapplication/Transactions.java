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
        PACE
    }

    public static void writeAthleteName(Context context, String athleteName){
        broadcastMessage(context, TRANSACTION_TYPE.NAME, athleteName.getBytes(StandardCharsets.UTF_8));
        return;
    }

    public static void writeHeartRate(Context context, int heartRate) {
        BigInteger bigInt = BigInteger.valueOf(heartRate);
        broadcastMessage(context, TRANSACTION_TYPE.HEART_RATE, bigInt.toByteArray());
        return;
    }

    public static void writeSpeed(Context context, double speed) {
        double speedRounded = Math.round(speed * 100.0) / 100.0;
        byte[] speedBytes = ByteBuffer.allocate(8).putDouble(speedRounded).array();
        broadcastMessage(context, TRANSACTION_TYPE.SPEED, speedBytes);
        return;
    }

    private static void broadcastMessage(Context context, TRANSACTION_TYPE transaction_type, byte[] data){
        final Intent intent = new Intent(TRANSACTION_ACTION);
        intent.putExtra(TRANSACTION_TYPES, transaction_type);
        intent.putExtra(DATA, data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
