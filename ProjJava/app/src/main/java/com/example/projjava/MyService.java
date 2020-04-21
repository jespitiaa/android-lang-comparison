package com.example.projjava;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.os.Trace;
import android.util.Log;
import android.widget.Toast;

import com.example.projjava.functions.binarytrees;
import com.example.projjava.functions.fannkuchredux;
import com.example.projjava.functions.fasta;
import com.example.projjava.functions.mandelbrot;
import com.example.projjava.functions.matrixdeterminant;
import com.example.projjava.functions.nbody;
import com.example.projjava.functions.reversecomplement;
import com.example.projjava.functions.spectralnorm;

public class MyService extends Service {

    private final static String TAG = "KotlinBencher";

    private final static String BINTREES = "projjava.binarytrees";
    private final static String FANNKUCH = "projjava.fannkuch";
    private final static String FASTA = "projjava.fasta";
    private final static String MANDELBROT = "projjava.mandelbrot";
    private final static String MATRIXDET = "projjava.matrixdeterminant";
    private final static String NBODY = "projjava.nbody";
    private final static String REVCOMP = "projjava.reversecomplement";
    private final static String SPECNORM = "projjava.spectralnorm";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent){
        Toast.makeText(this, "My Service Bound", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        String operation = intent.getStringExtra("function");
        Log.d(TAG, "onStart with "+operation);
        try {
            switch (operation){
                case BINTREES:
                    /* PARAMS:
                     * 1. maxdepth - Maximum depth for the binary trees
                     */
                    String[] args = {"21"};
                    binarytrees.main(args);
                    break;
                case FANNKUCH:
                    /* PARAMS:
                     * 1. N - number which will be factorialized. Must be at most 12
                     */
                    String[] args2 = {"12"};
                    fannkuchredux.main(args2);
                    break;
                case FASTA:
                    /* PARAMS:
                     * 1. Nchars - Number of chars for filling methods
                     */
                    String[] args3 = {"250000"};
                    fasta.main(args3);
                    break;
                case MANDELBROT:
                    /* PARAMS:
                     * 1. N - Number of repetitions
                     */
                    String[] args4 = {"16000"};
                    mandelbrot.main(args4);
                    break;
                case MATRIXDET:
                    /* PARAMS:
                     * 1. N - Matrix size
                     */
                    String[] args5 = {"10"};
                    matrixdeterminant.main(args5);
                    break;
                case NBODY:
                    /* PARAMS:
                     * 1. N - number of bodies
                     */
                    String[] args6 = {"5000"};
                    nbody.main(args6);
                    break;
                case REVCOMP:
                    /*
                     * 1. filename - Name for the file that will be used as inputstream
                     */
                    String[] args7 = {};
                    reversecomplement.main(args7);
                case SPECNORM:
                    /* PARAMS:
                     * 1. N - number which will be factorialized. Must be at most 12
                     */
                    String[] args8 = {"5500"};
                    spectralnorm.main(args8);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
