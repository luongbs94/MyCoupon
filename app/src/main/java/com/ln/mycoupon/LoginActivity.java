package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Company;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 3/30/16.
 */
public class LoginActivity extends AppCompatActivity {

    Button login, flogin, glogin;
    MaterialEditText username, password;
    LoveCouponAPI apiService;
    String TAG = "Coupon";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        setTitle(R.string.login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        apiService = MainApplication.getAPI();

        login = (Button) findViewById(R.id.login);
        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_user = username.getText().toString();
                String str_password = password.getText().toString();

                if(str_user.length() > 0 && str_password.length() > 0) {
                    getCompanyProfile(str_user, str_password);

                } else {
                    Snackbar.make(view, R.string.not_fill_login, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }


    public void getCompanyProfile(String user,String pass){
        Call<List<Company>> call = apiService.getCompanyProfile(user, pass);

        call.enqueue(new Callback<List<Company>>() {

            @Override
            public void onResponse(Call<List<Company>> arg0,
                                   Response<List<Company>> arg1) {
                List<Company> templates= arg1.body();

                SaveData.company = templates.get(0);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                MainApplication.editor.putBoolean(MainApplication.LOGINCOMPANY, true);
                MainApplication.editor.commit();

                finish();

            }

            @Override
            public void onFailure(Call<List<Company>> arg0, Throwable arg1) {
                Log.d(TAG,  "Failure");

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
