package com.huburt.app.common.net;



import com.huburt.app.common.ctx.ContextProvider;
import com.huburt.app.common.ctx.Debuggable;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by Yune on 2017/4/25.
 */

public class RetrofitFactory {
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            // 配置 client
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true);//错误重连
            if (Debuggable.isDebug()) {
                client.addInterceptor(new LogInterceptor());
            }

            // 配置 Retrofit
            Retrofit.Builder builder = new Retrofit.Builder();
            builder.baseUrl(ContextProvider.baseUrl())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client.build());

            retrofit = builder.build();
        }
        return retrofit;
    }
}
