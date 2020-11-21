package com.yjy.gdmap.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.yjy.gdmap.CallBack.UserCallback;
import com.yjy.gdmap.MainActivity;
import com.yjy.gdmap.R;
import com.yjy.gdmap.app.BaseActivity;
import com.yjy.gdmap.https.NetWorkManager;
import com.yjy.gdmap.info.User;

public class LoginMainActivity extends BaseActivity {
    EditText uid, pass;
    TextView back;
    Button  sigin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        back = findViewById(R.id.back);
        uid = findViewById(R.id.uid);
        pass = findViewById(R.id.pass);
        sigin = findViewById(R.id.sigin);
        back.setOnClickListener(view -> finish());
        sigin.setOnClickListener(v -> {
            if (pass.getText() != null && uid.getText() != null) {
                NetWorkManager manager = new NetWorkManager();
                manager.signIn(uid.getText().toString(), pass.getText().toString(), new UserCallback() {
                    @Override
                    public void onState(int state) {
                        runOnUiThread(() -> {
                            if (state == 0) {
                                Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();
                            } else if (state == 1) {
                                Toast.makeText(getApplicationContext(), "账号密码验证成功", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "账号未注册", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void signInSuccess(User user) {
                        runOnUiThread(() -> {
                            sUtils.setString("uname", user.getUname());
                            startActivity(new Intent(LoginMainActivity.this, MainActivity.class));
                            finish();
                        });
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "请输入密码或账号", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
