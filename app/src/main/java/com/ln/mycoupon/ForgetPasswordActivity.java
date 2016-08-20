package com.ln.mycoupon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ln.broadcast.ConnectivityReceiver;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText mEdtEmail;
    private Button mBtnSendEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        initViews();
        addEvents();
    }

    private void initViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_forget_password);

        mEdtEmail = (EditText) findViewById(R.id.edt_email);
    }

    private void addEvents() {
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectivityReceiver.isConnect()) {
                }
            }
        });
    }

    private void showMessages(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }
}
