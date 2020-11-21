package com.yjy.gdmap.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Enzo Cotter on 2020-11-16.
 */
public class SUtils {
    private SharedPreferences sharedPreferences;
    public SUtils(Context context){
        sharedPreferences =context.getSharedPreferences("jyj",Context.MODE_PRIVATE);
    }
    public void setString(String k,String v){
        sharedPreferences.edit().putString(k, v).apply();
    }
    public String getString(String k) {return sharedPreferences.getString(k,"0");}
}
