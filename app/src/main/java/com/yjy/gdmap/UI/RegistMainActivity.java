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
import com.yjy.gdmap.R;
import com.yjy.gdmap.app.BaseActivity;
import com.yjy.gdmap.https.NetWorkManager;
import com.yjy.gdmap.info.User;

public class RegistMainActivity extends BaseActivity {
    EditText uid, pass,repass,name;
    TextView back;
    Button regist, sigin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_regist);
        back = findViewById(R.id.back);
        uid = findViewById(R.id.uid);
        pass = findViewById(R.id.pass);
        repass=findViewById(R.id.repass);
        name=findViewById(R.id.name);
        regist =findViewById(R.id.register);
        back.setOnClickListener(view -> finish());
        regist.setOnClickListener(v->{
            if(pass.getText() != null && uid.getText() != null&&repass.getText() != null && name.getText() != null){
                if(pass.getText().toString().equals(repass.getText().toString())){
                    NetWorkManager manager = new NetWorkManager();
                    manager.register(new User(name.getText().toString(),
                            pass.getText().toString(),
                            uid.getText().toString()), new UserCallback() {
                        @Override
                        public void onState(int state) {
                            if(state==1){
                                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistMainActivity.this, LoginMainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),"注册失败，数据库中已经存在",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void signInSuccess(User user) {

                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"两次密码输入不一致",Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getApplicationContext(),"请全部输入",Toast.LENGTH_LONG).show();
            }

        });
    }
}
