package com.example.projkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class MyReceiver : BroadcastReceiver() {

    val TAG :String = "BroadcastReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        BencherHelper.runGC()
        BencherHelper.dumpHeap("/sdcard/prevkt.hprof")
        BencherHelper.logStart(TAG)

        val operation = intent.getStringExtra("function")
        val intent2 = Intent(context, MyService::class.java)
        intent2.putExtra("function", operation)
        Log.i(TAG, "waiting for 5 seconds for unplugging the phone")
        try {
            Thread.sleep(5000)
        } catch (e: Exception) {
            Log.i(TAG, "sleep time interrupted")
        }
        Log.i(TAG, "Launching service")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent2)
        } else {
            context.startService(intent2)
        }
        Log.i(TAG, "Received broadcast for $operation started")
    }
}
