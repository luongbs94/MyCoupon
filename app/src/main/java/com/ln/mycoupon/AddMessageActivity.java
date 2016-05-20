package com.ln.mycoupon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Message;
import com.rengwuxian.materialedittext.MaterialEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/7/16.
 */
public class AddMessageActivity extends AppCompatActivity {

    MaterialEditText title, content, link;
    CardView addMessage;
    LinearLayout layoutView;
    LoveCouponAPI apiService;
    String TAG = "Coupon";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (MaterialEditText) findViewById(R.id.title);
        content = (MaterialEditText) findViewById(R.id.content);
        link = (MaterialEditText) findViewById(R.id.link);
        layoutView = (LinearLayout) findViewById(R.id.layout_add_message);
        addMessage = (CardView) findViewById(R.id.cardview);


        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_title = title.getText().toString();
                String str_content = content.getText().toString();
                String str_link = link.getText().toString();
                if (str_title.length() > 0 && str_content.length() > 0 && str_link.length() > 0) {
                    addNews(str_title, str_content, str_link);
                } else {
                    Snackbar.make(view, R.string.not_fill_login, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        apiService = MainApplication.getAPI();

    }

    public void addNews(final String title, final String content, final String link) {
        Message template = new Message();
        template.setMessage_id(MainApplication.getRandomString(15));
        template.setContent(content);
        template.setLink(link);
        template.setTitle(title);
        template.setCompany_id(SaveData.company.company_id + "");


        //template.created_date= new Date();

        Call<Message> call2 = apiService.addMessage(template);
        call2.enqueue(new Callback<Message>() {

            @Override
            public void onResponse(Call<Message> arg0,
                                   Response<Message> arg1) {

                Snackbar.make(layoutView, R.string.add_message_success, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                AddMessageActivity.this.title.setText("");
                AddMessageActivity.this.content.setText("");
                AddMessageActivity.this.link.setText("");

            }

            @Override
            public void onFailure(Call<Message> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
                Log.d(TAG, "fail");
                Snackbar.make(layoutView, R.string.add_message_fail, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(3, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
