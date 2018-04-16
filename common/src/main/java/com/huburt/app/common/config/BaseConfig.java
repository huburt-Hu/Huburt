package com.huburt.app.common.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.util.Utils;
import com.huburt.app.common.ctx.ContextProvider;
import com.huburt.app.common.ctx.Debuggable;
import com.huburt.app.common.utils.AppManager;
import com.huburt.app.common.utils.CrashHandler;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import java.util.List;

import timber.log.Timber;

/**
 * Created by hubert on 2018/1/9.
 * <p>
 * app的基本配置，通过在manifest中添加meta启用配置：
 * <p>
 * {@code <meta-data
 * android:name="com.banban.app.common.base.delegate.BaseConfig"
 * android:value="Architecture_ConfigModule" />}
 */

public class BaseConfig extends ConfigModuleAdapter {
    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {
        lifecycles.add(new AppLifecyclesAdapter() {
            @Override
            public void onCreate(Application application) {
//                initARouter(application);
                ContextProvider.init(application);
                initLog(application);
                Utils.init(application);
            }
        });
    }

//    private void initARouter(Application application) {
//        if (Debuggable.isDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
//            ARouter.openLog();     // 打印日志
//            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        }
//        ARouter.init(application); // 尽可能早，推荐在Application中初始化
//    }


    private void initLog(Context context) {
        if (Debuggable.isDebug()) {
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                    .methodCount(3)         // (Optional) How many method line to show. Default 2
                    .methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
//                    .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                    .tag("CDL_APP")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    Logger.log(priority, tag, message, t);
                }
            });
        } else {
            CrashHandler.getInstance().init(context);
        }
    }

    protected String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }
        }
        return null;
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        //添加Activity管理
        lifecycles.add(new ActivityLifecycleCallbacksAdapter() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.getInstance().addActivity(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.getInstance().removeActivity(activity);
            }
        });
    }
}
