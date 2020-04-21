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
        Runtime.getRuntime().gc();

        try {
            Debug.dumpHprofData("/sdcard/prev.hprof");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d(TAG+"-START", Runtime.getRuntime().totalMemory()+
                " "+(Runtime.getRuntime().totalMemory() -Runtime.getRuntime().freeMemory())+
                " "+ Debug.getNativeHeapAllocatedSize()+
                " "+Debug.getPss());
        String operation = intent.getStringExtra("function");
        Intent intent2 = new Intent(context, MyService.class);
        intent2.putExtra("function", operation);
        Log.i(TAG, "waiting for 5 seconds for unplugging the phone");
        try{
            Thread.sleep(5000);
        }catch (Exception e){
            Log.i(TAG,"sleep time interrupted");
        }
        Log.i(TAG, "Launching service (API VERSION: "+ Build.VERSION.SDK_INT+")");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent2);
        } else {
            context.startService(intent2);
        }
        Log.i(TAG, "Received broadcast for "+operation+" started");
    }
}
