package com.example.projkotlin

import android.os.Debug
import android.util.Log
import java.io.IOException

object BencherHelper {
    fun runGC(){
        Runtime.getRuntime().gc()
    }
    fun logStart(tag:String){
        Log.d("$tag-START", ""+System.currentTimeMillis() +
                    " " + Runtime.getRuntime().totalMemory() +
                    " " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) +
                    " " + Debug.getNativeHeapAllocatedSize() +
                    " " + Debug.getPss())
    }
    fun logEnd(tag:String){
        Log.d("$tag-END", "" + System.currentTimeMillis()+
                    " " + Runtime.getRuntime().totalMemory() +
                    " " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) +
                    " " + Debug.getNativeHeapAllocatedSize() +
                    " " + Debug.getPss()
        )
    }
    fun dumpHeap(path:String){
        /*try {
            Debug.dumpHprofData(path)
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
    }
}