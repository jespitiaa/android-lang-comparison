package com.example.projjava;

import android.os.Debug;
import android.util.Log;

import java.io.IOException;

public class Bencher {
    private static final Bencher ourInstance = new Bencher();

    public static Bencher getInstance() {
        return ourInstance;
    }

    private Bencher() {}

    public void logStart(String tag){
        Log.d(tag+"-START", System.currentTimeMillis()+
                " "+Runtime.getRuntime().totalMemory()+
                " "+(Runtime.getRuntime().totalMemory() -Runtime.getRuntime().freeMemory())+
                " "+ Debug.getNativeHeapAllocatedSize()+
                " "+Debug.getPss());
    }
    public void logEndResults(String tag){
        Log.d(tag+"-END", System.currentTimeMillis()+
                " "+ Runtime.getRuntime().totalMemory()+
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
