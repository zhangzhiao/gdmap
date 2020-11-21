package com.yjy.gdmap.UI;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yjy.gdmap.R;
import com.yjy.gdmap.app.BaseActivity;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SplashMainActivity extends BaseActivity {
    private static final String[] permissionsGroup = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.WAKE_LOCK,
            };

    Button join_us;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        rxper();
        join_us = findViewById(R.id.join_us);
        login = findViewById(R.id.login);
        join_us.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),RegistMainActivity.class);
            startActivity(intent);
        });
        login.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),LoginMainActivity.class);
            startActivity(intent);
        });
    }

    private void rxper() {
//        创建 RxPermissions 实例
        RxPermissions rxPermissions = new RxPermissions(SplashMainActivity.this);
        rxPermissions.request(permissionsGroup)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                    if (aBoolean) {
                        Toast.makeText(SplashMainActivity.this, "已获取权限", Toast.LENGTH_LONG)
                                .show();
                    } else {
//只有用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                            Toast.makeText(SplashMainActivity.this, "权限被禁止", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
}


}
