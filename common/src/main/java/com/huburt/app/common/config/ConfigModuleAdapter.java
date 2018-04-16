package com.huburt.app.common.config;

import android.app.Application;
import android.content.Context;

import java.util.List;

/**
 * Created by hubert on 2018/1/16.
 */

public abstract class ConfigModuleAdapter implements ConfigModule {

    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {

    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {

    }
}
