package com.huburt.app.huburt.api;


import android.arch.lifecycle.LiveData;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by hubert on 2018/4/13.
 */

public interface MzituService {
    String URL_NAME = "hot_girl";
    String URL = "http://www.mzitu.com/";

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("page/{page}/")
    LiveData<String> index(@Path("page") int page);

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("hot/page/{page}/")
    LiveData<String> hot(@Path("page") int page);

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("best/page/{page}/")
    LiveData<String> best(@Path("page") int page);

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("xinggan/page/{page}/")
    LiveData<String> sexy(@Path("page") int page);

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("japan/page/{page}/")
    LiveData<String> japan(@Path("page") int page);

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("taiwan/page/{page}/")
    LiveData<String> taiwan(@Path("page") int page);

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("mm/page/{page}/")
    LiveData<String> mm(@Path("page") int page);

    @Headers({RetrofitUrlManager.DOMAIN_NAME_HEADER + URL_NAME})
    @GET("{id}")
    LiveData<String> imageList(@Path("id") int id);
}
