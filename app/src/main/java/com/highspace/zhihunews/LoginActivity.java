package com.highspace.zhihunews;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText mAccountEt;
    private EditText mPasswordEt;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        // Bmob.initialize(this, "03487e0e22a3e6e1aef9506e4f1c32b5");
        initView();
        initEvent();
    }

    private void initView() {
        mAccountEt = (EditText) findViewById(R.id.login_activity_account_et);
        mPasswordEt = (EditText) findViewById(R.id.login_activity_password_et);
        mLoginBtn = (Button) findViewById(R.id.login_activity_login_btn);

    }

    private void initEvent() {
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
