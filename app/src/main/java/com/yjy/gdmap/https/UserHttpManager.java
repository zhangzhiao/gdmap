package com.yjy.gdmap.https;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Enzo Cotter on 2020-11-09.
 */
public class UserHttpManager {
    private Retrofit mRetrofit;
    private HashMap<Class, Retrofit> mServiceHashMap = new HashMap<>();

    public UserHttpManager() {
        // init okhttp
        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        // init retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://118.31.6.163:7070/")
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
