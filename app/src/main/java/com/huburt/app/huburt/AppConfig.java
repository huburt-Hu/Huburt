package com.huburt.app.huburt;

import android.app.Application;
import android.content.Context;

import com.huburt.app.common.config.AppLifecycles;
import com.huburt.app.common.config.AppLifecyclesAdapter;
import com.huburt.app.common.config.ConfigModuleAdapter;
import com.huburt.app.huburt.api.MzituService;

import java.util.List;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import timber.log.Timber;

/**
 * Created by hubert on 2018/4/13.
 */

public class AppConfig extends ConfigModuleAdapter {

    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {
        lifecycles.add(new AppLifecyclesAdapter() {
            @Override
            public void onCreate(Application application) {
                RetrofitUrlManager.getInstance()
                        .putDomain(MzituService.URL_NAME, MzituService.URL);

                //计算可使用的最大内存
                long maxMemory = Runtime.getRuntime().maxMemory();
                Timber.i("" + maxMemory);
            }
        });
    }
}
