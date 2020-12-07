package com.yjy.gdmap.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String getTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("HH");
            return simpleDateFormat.format(new Date());
        }

}
