package com.ln.mycoupon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.views.MaterialEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private MaterialEditText mEdtEmail;

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

        mEdtEmail = (MaterialEditText) findViewById(R.id.edt_email);
    }

    private void addEvents() {

        mEdtEmail.addTextChangedListener(new Events(mEdtEmail));
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ConnectivityReceiver.isConnect()) {
                    showMessages(R.string.check_network);
                    return;
                }

                String email = mEdtEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    mEdtEmail.setError(getString(R.string.user_dont_empty));
                    return;
                }

                if (!isEmailValid(email)) {
                    mEdtEmail.setError(getString(R.string.email_do_not_match));
                    return;
                }

                SendEmail send = new SendEmail(email);
                Call<String> sendEmail = MainApplication.getAPI().sendPassword(send);
                sendEmail.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String content = getString(R.string.send_email_fails);
                        if (response.body() != null) {
                            content = getString(R.string.send_email_success);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

    private class Events implements TextWatcher {

        private View view;

        public Events(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (view == mEdtEmail) {
                if (!isEmailValid(mEdtEmail.getText().toString().trim())) {
                    mEdtEmail.setError(getString(R.string.email_do_not_match));
                } else {
                    mEdtEmail.setError("");
                }
            }
        }
    }
}
