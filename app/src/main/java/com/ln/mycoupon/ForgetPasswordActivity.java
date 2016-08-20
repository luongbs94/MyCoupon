package com.ln.mycoupon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText mEdtEmail;

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
                    showMessages(R.string.check_network);
                    return;
                }

                String email = mEdtEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    showMessages(R.string.user_dont_empty);
                    return;
                }

                if (!isEmailValid(email)) {
                    showMessages(R.string.email_do_not_match);
                    return;
                }

                SendEmail send = new SendEmail(email);
                Call<String> sendEmail = MainApplication.getAPI().sendPassword(send);
                sendEmail.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String content = "Send Email fail";
                        if (response.body() != null) {
                            content = "Password was sent to your email";
                        }
                        new MaterialDialog.Builder(ForgetPasswordActivity.this)
                                .content(content)
                                .positiveText(R.string.agree)
                                .positiveColor(getResources().getColor(R.color.title_bg))
                                .show();

                        Log.d("SendEmail", response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("SendEmail", "onFailure" + t.toString());
                    }
                });

            }
        });
    }

    private void showMessages(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static class SendEmail {
        private String value;

        public SendEmail(String value) {
            this.value = value;
        }
    }
}
