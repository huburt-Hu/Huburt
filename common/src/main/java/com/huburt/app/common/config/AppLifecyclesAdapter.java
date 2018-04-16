package com.huburt.app.common.config;

import android.app.Application;
import android.content.Context;

/**
 * Created by hubert on 2018/1/16.
 *
 * AppLifecycles接口的适配器
 */

public abstract class AppLifecyclesAdapter implements AppLifecycles {
    @Override
    public void attachBaseContext(Context base) {

    }

    @Override
    public void onCreate(Application application) {

    }

    @Override
    public void onTerminate(Application application) {

    }
}
