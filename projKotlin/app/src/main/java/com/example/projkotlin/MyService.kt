package com.example.projkotlin

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Debug
import android.os.IBinder
import android.os.Trace
import android.util.Log
import android.widget.Toast
import com.example.projkotlin.functions.*

const val BINTREES = "projkotlin.binarytrees"
const val FANNKUCH = "projkotlin.fannkuch"
const val FASTA = "projkotlin.fasta"
const val MANDELBROT = "projkotlin.mandelbrot"
const val MATRIXDET = "projkotlin.matrixdeterminant"
const val NBODY = "projkotlin.nbody"
const val SPECNORM = "projkotlin.spectralnorm"

class MyService : Service() {
    private val TAG = "KotlinBencher"
    override fun onBind(intent: Intent): IBinder? {
        Toast.makeText(this, "My Service Bound", Toast.LENGTH_LONG).show()
        Log.d(TAG, "onBind")
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show()
        Log.d(TAG, "onDestroy")
    }

    override fun onStart(intent: Intent, startid: Int) {
        val operation = intent.getStringExtra("function")
        Toast.makeText(this, "Testing $operation", Toast.LENGTH_LONG).show()
        Log.d(TAG, "onStart with $operation")
        try{

            when(operation){
                BINTREES->{
                    /* PARAMS:
                     * 1. maxdepth - Maximum depth for the binary trees
                     */
                    val args = arrayOf("21")
                    binarytrees.execute(args)
                }
                FANNKUCH->{
                    /* PARAMS:
                     * 1. N - number which will be factorialized. Must be at most 12
                     */
                    val args = arrayOf("12")
                    fannkuchredux.execute(args)
                }
                FASTA->{
                    /* PARAMS:
                     * 1. Nchars - Number of chars for filling methods
                     */
                    val args = arrayOf("250000")
                    fasta.execute(args)
                }
                MANDELBROT->{
                    /* PARAMS:
                     * 1. N - Number of repetitions
                     */
                    val args = arrayOf("16000")
                    mandelbrot.execute(args)
                }
                MATRIXDET->{
                    /* PARAMS:
                     * 1. N - Matrix size
                     */
                    val args = arrayOf("10")
                    matrixdeterminant.execute(args)
                }
                NBODY->{
                    /* PARAMS:
                     * 1. N - Number of bodies
                     */
                    val args = arrayOf("5000")
                    nbody.execute(args)
                }
                SPECNORM->{
                    /* PARAMS:
                     * 1. N - Number of bodies
                     */
                    val args = arrayOf("5500")
                    spectralnorm.execute(args)
                }
            }
        }catch (e:Exception){
            e.printStackTrace();
        }
    }
}
