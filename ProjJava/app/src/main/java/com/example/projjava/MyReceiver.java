package com.example.projjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import java.io.IOException;

public class MyReceiver extends BroadcastReceiver {
    private final static String TAG = "broadcastR";
    @Override
    public void onReceive(Context context, Intent intent) {
        Bencher.getInstance().runGC();
        Bencher.getInstance().logStart(TAG);
        Bencher.getInstance().dumpHeap("/sdcard/prev.hprof");

        String operation = intent.getStringExtra("function");
        Intent intent2 = new Intent(context, MyService.class);
        intent2.putExtra("function", operation);
        Log.i(TAG, "Launching service (API VERSION: "+ Build.VERSION.SDK_INT+")");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent2);
        } else {
            context.startService(intent2);
        }
        Log.i(TAG, "Received broadcast for "+operation+" started");
    }
}
