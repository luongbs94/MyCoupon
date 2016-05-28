package com.ln.mycoupon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.ln.adapter.SelectedImageAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.ItemImage;
import com.ln.model.Message;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/7/16.
 */
public class AddMessageActivity extends AppCompatActivity {

    private static final Firebase ROOT =
            new Firebase("https://nhahv-firebase.firebaseio.com/");
    private String TAG = getClass().getSimpleName();

    private MaterialEditText title, content, link;
    private CardView addMessage;
    private LinearLayout layoutView;
    private LoveCouponAPI apiService;
    private ImageView mImgSelectImages;
    private RecyclerView mRvSelectImages, mRvShow;

    private static final int mSelectNumber = 9;
    private static final int mMode = 1;
    private static final boolean isShow = true;
    private static final boolean isPreview = true;
    private static final boolean isCrop = false;
    private boolean isUpload;
    private static final int WIDTH = 100;

    private ArrayList<ItemImage> mImages = new ArrayList<>();
    private SelectedImageAdapter mSelectedImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initViews();

        addEvents();

        apiService = MainApplication.getAPI();

    }

    private void initViews() {
        title = (MaterialEditText) findViewById(R.id.title);
        content = (MaterialEditText) findViewById(R.id.content);
        link = (MaterialEditText) findViewById(R.id.link);
        layoutView = (LinearLayout) findViewById(R.id.layout_add_message);
        addMessage = (CardView) findViewById(R.id.card_view_add_messages);
        mImgSelectImages = (ImageView) findViewById(R.id.img_selected_images);

        mRvSelectImages = (RecyclerView) findViewById(R.id.rec_select_images);
        mRvSelectImages.setLayoutManager(new GridLayoutManager(this, 3));

        mSelectedImageAdapter = new SelectedImageAdapter(getApplicationContext(), mImages);
        mRvSelectImages.setAdapter(mSelectedImageAdapter);
    }


    private void addEvents() {
        addMessage.setOnClickListener(new Events());
        mImgSelectImages.setOnClickListener(new Events());
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);

            for (String s : images) {
                mImages.add(new ItemImage(s));
            }
            // do something
            mSelectedImageAdapter.notifyDataSetChanged();
            isUpload = false;
        }
    }

    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.card_view_add_messages:
                    onClickAddMessages(v);
                    break;
                case R.id.img_selected_images:
                    onClickSelectImages();
                    break;
            }
        }

        private void onClickAddMessages(View view) {

            if (!isUpload) {

                String linkCompany = MainApplication.getRandomString(12);
                String str_title = title.getText().toString();
                String str_content = content.getText().toString();
                String str_link = link.getText().toString();
                if (str_title.length() > 0 && str_content.length() > 0 && str_link.length() > 0) {
                    addNews(str_title, str_content, str_link);
                } else {
                    Snackbar.make(view, R.string.not_fill_login, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                for (int i = 0; i < mImages.size(); i++) {
                    String string = convertBase64(mImages.get(i).getPath());

                    if (i == mImages.size() - 1) {
                        ROOT.child("user/" + "coupon").push().setValue(string, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                getShowToast("Upload Success");
                            }
                        });
                    } else {
                        ROOT.child("user/" + "coupon").push().setValue(string);
                    }
                }

                isUpload = true;
            }

        }

        private void onClickSelectImages() {
            ImageSelectorActivity.start(AddMessageActivity.this, mSelectNumber, mMode, isShow, isPreview, isCrop);
        }
    }

    private void getShowToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private String convertBase64(final String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        int height = WIDTH * bitmap.getHeight() / bitmap.getWidth();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, WIDTH, height, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String title = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        return title;
    }
}
