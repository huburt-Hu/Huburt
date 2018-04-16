package com.huburt.app.common;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.huburt.app.common.config.AppDelegate;
import com.huburt.app.common.config.AppLifecycles;
import com.huburt.app.common.config.BaseConfig;
import com.huburt.app.common.config.ConfigModule;
import com.huburt.app.common.config.ConfigModuleAdapter;
import com.huburt.app.common.config.ManifestParser;
import com.huburt.app.common.ctx.Debuggable;


/**
 * Created by hubert on 2018/3/14.
 * <p>
 * 应用的Application，初始化了{@link Debuggable}，基本上不需要改动该类。
 * <p>
 * 各个模块的初始化不要在此类中添加，可以实现{@link ConfigModule}接口或者其适配器{@link ConfigModuleAdapter}
 * 在对应的inject方法中注入{@link AppLifecycles}的onCreate方法中实现初始化，可以参照{@link BaseConfig}的实现。
 * <p>
 * 然后在module的Manifest的Application下添加meta-data节点：
 * {@code <meta-data
 * android:name="com.banban.app.common.base.delegate.BaseConfig"
 * android:value="Architecture_ConfigModule" />}
 * <p>
 * name声明为ConfigModule实现类，value固定为Architecture_ConfigModule，用于识别为Application的Config。
 * <p>
 * 该meta-data会通过{@link ManifestParser}找到并解析成实例，并通过{@link AppDelegate}分发生命周期。
 */

public class App extends MultiDexApplication {

    private AppLifecycles mAppDelegate;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Debuggable.init(base);
        if (mAppDelegate == null)
            this.mAppDelegate = new AppDelegate(base);
        this.mAppDelegate.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mAppDelegate != null)
            this.mAppDelegate.onCreate(this);
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mAppDelegate != null)
            this.mAppDelegate.onTerminate(this);
    }
}
