package com.yjy.gdmap.https;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by hzy on 2019/1/10
 * 网络请求
 **/
public class HttpManager {

    private Retrofit mRetrofit;
    private HashMap<Class, Retrofit> mServiceHashMap = new HashMap<>();

    public HttpManager() {
        // init okhttp
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        // init retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.uomg.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mServiceHashMap.put(CommonService.class, mRetrofit);
    }


    public <T> T getService(Class<T> clz) {
        Retrofit retrofit = mServiceHashMap.get(clz);
        if (retrofit != null) {
            return retrofit.create(clz);
        } else {
            return null;
        }
    }
}