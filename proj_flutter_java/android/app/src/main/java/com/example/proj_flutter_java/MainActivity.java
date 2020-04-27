package com.example.proj_flutter_java;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import java.io.IOException;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;


public class MainActivity extends FlutterActivity {
  private final static String CHANNEL = "com.example.flutter/bencher";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
            new MethodChannel.MethodCallHandler() {
              @Override
              public void onMethodCall(MethodCall call, MethodChannel.Result result) {
                if (call.method.equals("logStart")) {
                  logStart(call.argument("tag"));
                }
                else if (call.method.equals("logEnd")) {
                  logEndResults(call.argument("tag"));
                }
                else if (call.method.equals("runGC")) {
                  runGC();
                }
                else if (call.method.equals("dumpHprof")) {
                  dumpHeap(call.argument("path"));
                }
              }});
  }

  public void logStart(String tag){
    Log.d(tag+"-START", System.currentTimeMillis()+
            " "+Runtime.getRuntime().totalMemory()+
            " "+(Runtime.getRuntime().totalMemory() -Runtime.getRuntime().freeMemory())+
            " "+ Debug.getNativeHeapAllocatedSize()+
            " "+Debug.getPss());
  }
  public void logEndResults(String tag){
    Log.d(tag+"-END", ""+ System.currentTimeMillis()+
            " "+Runtime.getRuntime().totalMemory()+
            " "+(Runtime.getRuntime().totalMemory() -Runtime.getRuntime().freeMemory())+
            " "+Debug.getNativeHeapAllocatedSize()+
            " "+Debug.getPss());
  }
  public void dumpHeap(String path){
    try {
      Debug.dumpHprofData(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void runGC(){
    Runtime.getRuntime().gc();
  }

}
