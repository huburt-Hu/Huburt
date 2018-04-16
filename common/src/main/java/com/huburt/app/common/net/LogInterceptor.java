package com.huburt.app.common.net;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import timber.log.Timber;

/**
 * Created by Yune on 2017/4/25.
 */

public class LogInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();

        Buffer buffer = new Buffer();
        RequestBody body = request.body();
        if (body != null)
            body.writeTo(buffer);
        Timber.e("Sending request %s on %s%n%sRequest Params: %s \n URLEncode: %s",
                request.url(), chain.connection(), request.headers(),
                URLDecoder.decode(buffer.clone().readUtf8(), "UTF-8"), buffer.clone().readUtf8());
        buffer.close();

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();

        BufferedSource source = response.body().source();
        source.request(Long.MAX_VALUE);
        buffer = source.buffer().clone();
        Timber.e("Received response for %s in %.1fms%n%sResponse Json: %s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers(),
                buffer.readUtf8());

        return response;
    }
}
