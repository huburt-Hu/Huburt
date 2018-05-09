package com.huburt.app.common.executor;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by julie on 2017/3/1.
 */

public class AsyncTaskExecutorImpl implements AsyncTaskExecutor {

    private final int poolSize = Runtime.getRuntime().availableProcessors() + 1;
    private ScheduledExecutorService mThreadPool = Executors.newScheduledThreadPool(poolSize);

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void runOnBackground(Runnable runnable) {
        if (runnable != null) {
            mThreadPool.submit(runnable);
        }
    }

    @Override
    public void runOnBackgroundDelay(Runnable runnable, long delayMillis) {
        if (runnable != null) {
            mThreadPool.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void runOnUiThread(Runnable runnable) {
        if (runnable != null) {
            mHandler.post(runnable);
        }
    }

    @Override
    public void runOnUiThreadDelay(Runnable runnable, long delayMillis) {
        if (runnable != null) {
            mHandler.postDelayed(runnable, delayMillis);
        }
    }
}
