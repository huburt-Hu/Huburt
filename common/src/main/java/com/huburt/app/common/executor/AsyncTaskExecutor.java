package com.huburt.app.common.executor;

/**
 * Created by julie on 2017/3/1.
 */

public interface AsyncTaskExecutor {
    void runOnBackground(Runnable runnable);

    void runOnBackgroundDelay(Runnable runnable, long delayMillis);

    void runOnUiThread(Runnable runnable);

    void runOnUiThreadDelay(Runnable runnable, long delayMillis);
}
