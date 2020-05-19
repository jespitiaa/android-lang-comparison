package com.example.projkotlin.functions
import android.util.Log
import com.example.projkotlin.BencherHelper
import java.io.BufferedOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

private val TAG = "mandelbrotkt"

fun main(args: Array<String>) {
    mandelbrot.execute(args)
    BencherHelper.logEnd(TAG)
    BencherHelper.dumpHeap("/sdcard/$TAG.hprof")
    BencherHelper.runGC()
}

object mandelbrot {
    internal var out: Array<ByteArray> = arrayOf()
    internal var yCt: AtomicInteger = AtomicInteger()
    internal var Crb: DoubleArray = doubleArrayOf()
    internal var Cib: DoubleArray = doubleArrayOf()

    internal fun getByte(x: Int, y: Int): Int {
        var res = 0
        var i = 0
        while (i < 8) {
            var Zr1 = Crb[x + i]
            var Zi1 = Cib[y]

            var Zr2 = Crb[x + i + 1]
            var Zi2 = Cib[y]

            var b = 0
            var j = 49
            do {
                val nZr1 = Zr1 * Zr1 - Zi1 * Zi1 + Crb[x + i]
                val nZi1 = Zr1 * Zi1 + Zr1 * Zi1 + Cib[y]
                Zr1 = nZr1
                Zi1 = nZi1

                val nZr2 = Zr2 * Zr2 - Zi2 * Zi2 + Crb[x + i + 1]
                val nZi2 = Zr2 * Zi2 + Zr2 * Zi2 + Cib[y]
                Zr2 = nZr2
                Zi2 = nZi2

                if (Zr1 * Zr1 + Zi1 * Zi1 > 4) {
                    b = b or 2
                    if (b == 3) break
                }
                if (Zr2 * Zr2 + Zi2 * Zi2 > 4) {
                    b = b or 1
                    if (b == 3) break
                }
            } while (--j > 0)
            res = (res shl 2) + b
            i += 2
        }
        return res xor -1
    }

    internal fun putLine(y: Int, line: ByteArray) {
        for (xb in line.indices)
            line[xb] = getByte(xb * 8, y).toByte()
    }

    @Throws(Exception::class)
    @JvmStatic
    fun execute(args: Array<String>) {
        var N = 6000
        if (args.size >= 1) N = Integer.parseInt(args[0])

        Crb = DoubleArray(N + 7)
        Cib = DoubleArray(N + 7)
        val invN = 2.0 / N
        for (i in 0 until N) {
            Cib[i] = i * invN - 1.0
            Crb[i] = i * invN - 1.5
        }
        yCt = AtomicInteger()
        out = Array(N) { ByteArray((N + 7) / 8) }

        val pool = arrayOfNulls<Thread>(2 * Runtime.getRuntime().availableProcessors())

        val startSignal = CountDownLatch(1)
        val stopSignal = CountDownLatch(pool.size - 1)

        for (i in pool.indices) {
            var nuevo = Thread(Worker(startSignal, stopSignal, yCt, out))
            pool[i] = nuevo
            nuevo.start()
            Log.d("abr", "is started" + pool[i]!!.isAlive())
        }
        Log.d("abr", "pool "+pool.size)
        startSignal.countDown()
        Log.d("inicio", "Se supone que ya estan arriba")
        stopSignal.await()
        Log.d("fin", "Se supone que acabaron todos")

        val stream = BufferedOutputStream(System.out)
        stream.write("P4\n$N $N\n".toByteArray())
        //for (i in 0 until N) stream.write(out[i])
        BencherHelper.logEnd(TAG)
        BencherHelper.dumpHeap("/sdcard/$TAG.hprof")
        BencherHelper.runGC()
        stream.close()
    }
}

internal class Worker(
    private val startSignal: CountDownLatch,
    private val doneSignal: CountDownLatch,
    private val yCt: AtomicInteger,
    private val out: Array<ByteArray>
) : Runnable {
    override fun run() {
        try {
            startSignal.await()
            Log.d("worker", "buena llego la señal")
            doWork()
            Log.d("abr", "Despues del dowork")
            doneSignal.countDown()
            Log.d("abr", "Despues del countdown")
        } catch (ex: InterruptedException) {
        }
        // return;
    }

    fun doWork() {
        Log.d("trabajando en", Thread.currentThread().name)
        Log.d("datos", "yct=" + yCt + ", outln=" + out.size)
        var y: Int = mandelbrot.yCt.getAndIncrement()
        while (y < mandelbrot.out.size) {
            mandelbrot.putLine(y, mandelbrot.out[y])
            y = mandelbrot.yCt.getAndIncrement()
        }
    }
}
