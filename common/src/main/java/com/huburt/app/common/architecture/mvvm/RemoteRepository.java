package com.huburt.app.common.architecture.mvvm;

import com.huburt.app.common.ctx.Debuggable;
import com.huburt.app.common.net.LogInterceptor;
import com.huburt.app.common.net.RetrofitHelper;
import com.huburt.app.common.net.StringConverterFactory;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by hubert on 2018/4/13.
 */

public class RemoteRepository {

    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder = RetrofitUrlManager.getInstance().with(builder);
            if (Debuggable.isDebug()) {
                builder.addInterceptor(new LogInterceptor());
            }
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(StringConverterFactory.create())
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .client(builder.build())
                    .build();
        }
        return retrofit;
    }

    public static <T> T getService(Class<T> cls) {
        return RetrofitHelper.getService(getRetrofit(), cls);
    }
}
