package com.huangxueqin.gclient.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huangxueqin on 2018/4/26.
 */

public class TaskScheduler {

    private int mCpuCoreNumber;
    private ThreadPoolExecutor mExecutor;
    private Handler mMainHandler;
    private static volatile TaskScheduler sInstance;

    public static TaskScheduler getInstance() {
        if (sInstance == null) {
            synchronized (TaskScheduler.class) {
                if (sInstance == null) {
                    sInstance = new TaskScheduler();
                }
            }
        }
        return sInstance;
    }

    private TaskScheduler() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mCpuCoreNumber = getCpuCoreNumber();
        mExecutor = createExecutor(mCpuCoreNumber);
    }

    public void executeAsync(Runnable r) {
        mExecutor.execute(r);
    }

    public void runOnUIThread(Runnable r) {
        mMainHandler.post(r);
    }

    public void runOnUIThread(Runnable r, long delay) {
        mMainHandler.postDelayed(r, delay);
    }

    private ThreadPoolExecutor createExecutor(int cpuCoreNumber) {
        int minCoreSize = Math.min(4, Math.max(2, cpuCoreNumber));
        int maxCoreSize = Math.max(cpuCoreNumber*2+1, 4);
        int keepAliveSeconds = 30;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>();
        ThreadFactory threadFactory = new TaskThreadFactory();
        boolean allowCoreTimeout = true;
        ThreadPoolExecutor executor =  new ThreadPoolExecutor(minCoreSize,
                maxCoreSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                workQueue,
                threadFactory);
        executor.allowCoreThreadTimeOut(allowCoreTimeout);
        return executor;
    }

    /**
     * 粗略实现，一般是通过查看"/sys/devices/system/cpu/"下面的文件来判断核心数,
     * 通过Runtime方法来判断，得到的结果并不一定准确，它只是反映了JVM当前可用的核心数
     * @return
     */
    private int getCpuCoreNumber() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return 2;
        }
    }

    private static class TaskThreadFactory implements ThreadFactory {

        private final AtomicInteger mCount = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "TaskThread #" + mCount.getAndIncrement());
        }
    }
}
