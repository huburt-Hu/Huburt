package com.huburt.app.common.net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by hubert on 2018/5/23.
 *
 * token过期自动刷新拦截器
 */
public class TokenInterceptor implements Interceptor {

    private long refreshTime;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        // try the request
        Response originalResponse = chain.proceed(request);
        BufferedSource source = originalResponse.body().source();
        source.request(Long.MAX_VALUE);
        Buffer buffer = source.buffer().clone();
        String bodyString = buffer.readUtf8();
        //判断token是否过期
        if (needRefreshToken(bodyString)) {
            //同步请求token
            String newToken = getNewToken();
            // create a new request and modify it accordingly using the new token
            Request newRequest = request.newBuilder()
                    .header("token", newToken)
                    .build();
            buffer.close();
            return chain.proceed(newRequest);
        }
        return originalResponse;
    }

    private String token;//模拟缓存token

    private synchronized String getNewToken() {
        if (refreshTime == 0) {
            refreshTime = System.currentTimeMillis();
            String newToken = "";//todo 同步获取新token
            return token = newToken;
        } else {
            return token;//todo 获取缓存的新token
        }
    }

    private boolean needRefreshToken(String bodyString) {
        // TODO 判断token是否过期
        return true;
    }
}
