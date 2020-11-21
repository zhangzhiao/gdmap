package com.yjy.gdmap.https;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Enzo Cotter on 2020-11-07.
 */
public interface CommonService {
    @POST("register")
    Call<ResponseBody> register(@Query("id") String id, @Query("password") String password, @Query("name") String name);
    @POST("haskey")
    Call<ResponseBody> isRegister(@Query("key") String key);
    @POST("signin")
    Call<ResponseBody> signin(@Query("id") String id, @Query("password") String password);
}
