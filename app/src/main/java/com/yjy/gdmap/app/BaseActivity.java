package com.yjy.gdmap.app;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.yjy.gdmap.util.SUtils;

/**
 * Created by Enzo Cotter on 2020-11-16.
 */
public class BaseActivity extends AppCompatActivity {
    public SUtils sUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sUtils =new SUtils(this);
    }
}
