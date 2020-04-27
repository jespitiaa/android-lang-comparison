/**
 * The Computer Language Benchmarks Game
 * https://salsa.debian.org/benchmarksgame-team/benchmarksgame/
 *
 * based on Jarkko Miettinen's Java program
 * contributed by Tristan Dupont
 * *reset*
 */
package com.example.projjava.functions;
import android.os.Build;
import android.os.CpuUsageInfo;
import android.os.Debug;
import android.os.Trace;
import android.util.Log;

import com.example.projjava.Bencher;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class binarytrees {

    private static final int MIN_DEPTH = 4;
    private static final ExecutorService EXECUTOR_SERVICE =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final String TAG = "binary-trees";

    public static void main(final String[] args) throws Exception {
        
        Log.d(TAG, "actually enters the method");

        int n = 0;
        if (0 < args.length) {
            n = Integer.parseInt(args[0]);
        }

        final int maxDepth = n < (MIN_DEPTH + 2) ? MIN_DEPTH + 2 : n;
        final int stretchDepth = maxDepth + 1;

        Log.d(TAG,"stretch tree of depth " + stretchDepth + "\t check: "
                + bottomUpTree( stretchDepth).itemCheck());


        final TreeNode longLivedTree = bottomUpTree(maxDepth);


        final String[] results = new String[(maxDepth - MIN_DEPTH) / 2 + 1];


        for (int d = MIN_DEPTH; d <= maxDepth; d += 2) {
            final int depth = d;
            EXECUTOR_SERVICE.execute(() -> {
                int check = 0;

                final int iterations = 1 << (maxDepth - depth + MIN_DEPTH);
                for (int i = 1; i <= iterations; ++i) {
                    final TreeNode treeNode1 = bottomUpTree(depth);
                    check += treeNode1.itemCheck();
                }
                results[(depth - MIN_DEPTH) / 2] =
                        iterations + "\t trees of depth " + depth + "\t check: " + check;
            });
        }
        Log.d (TAG, "survives first loop");

        for (final String str : results) {
            if(str!=null){
                Log.d(TAG,str);
            }
        }

        Log.d(TAG,"long lived tree of depth " + maxDepth +
                "\t check: " + longLivedTree.itemCheck());

        Bencher.getInstance().logEndResults(TAG);
        Bencher.getInstance().dumpHeap("/sdcard/bintrees.hprof");
        Bencher.getInstance().runGC();

        EXECUTOR_SERVICE.shutdown();
        EXECUTOR_SERVICE.awaitTermination(50L, TimeUnit.SECONDS);

    }

    private static TreeNode bottomUpTree(final int depth) {
        if (0 < depth) {
            return new TreeNode(bottomUpTree(depth - 1), bottomUpTree(depth - 1));
        }
        return new TreeNode();
    }

    private static final class TreeNode {

        private final TreeNode left;
        private final TreeNode right;

        private TreeNode(final TreeNode left, final TreeNode right) {
            this.left = left;
            this.right = right;
        }

        private TreeNode() {
            this(null, null);
        }

        private int itemCheck() {
            // if necessary deallocate here
            if (null == left) {
                return 1;
            }
            return 1 + left.itemCheck() + right.itemCheck();
        }
    }
}