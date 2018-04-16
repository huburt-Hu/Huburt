package com.huburt.app.common.net;

import java.util.HashMap;

import retrofit2.Retrofit;

/**
 * Created by hubert on 2018/3/23.
 */

public final class RetrofitHelper {

    public static final String DIVIDER = ":";
    private static HashMap<String, Object> serviceMap = new HashMap<>();

    private RetrofitHelper() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> cls) {
        Object service = serviceMap.get(cls.getCanonicalName());
        if (service == null) {
            service = RetrofitFactory.getRetrofit().create(cls);
            serviceMap.put(cls.getCanonicalName(), service);
        }
        return (T) service;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Retrofit retrofit, Class<T> cls) {
        String key = retrofit.hashCode() + DIVIDER + cls.getCanonicalName();
        Object service = serviceMap.get(key);
        if (service == null) {
            service = retrofit.create(cls);
            serviceMap.put(key, service);
        }
        return (T) service;
    }
}
