package com.ln.mycoupon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ln.adapter.SelectedImageAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.ItemImage;
import com.ln.model.Message;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/7/16.
 * <></>
 */
public class AddMessageActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();

    private MaterialEditText mTxtTitle, mTxtContent, mTxtLink;
    private CardView addMessage;
    private LinearLayout layoutView;
    private ImageView mImgSelectImages;
    private RecyclerView mRecyclerViewImages;

    private static final int mSelectNumber = 9;
    private static final int mMode = 1;
    private static final boolean isShow = true;
    private static final boolean isPreview = true;
    private static final boolean isCrop = false;

    private List<ItemImage> mListImagesSelected = new ArrayList<>();
    private List<String> mListStrImages = new ArrayList<>();
    private SelectedImageAdapter mSelectedImageAdapter;

    private LoveCouponAPI mApiService;
    private LoveCouponAPI apiService;

    private boolean mIsShowRecyclerView;

    private String mLinkImageNews;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiService = MainApplication.getAPI();
        mApiService = MainApplication.getAPI1();

        initViews();

        addEvents();
    }

    private void initViews() {

        mTxtTitle = (MaterialEditText) findViewById(R.id.title);
        mTxtContent = (MaterialEditText) findViewById(R.id.content);
        mTxtLink = (MaterialEditText) findViewById(R.id.link);
        layoutView = (LinearLayout) findViewById(R.id.layout_add_message);
        addMessage = (CardView) findViewById(R.id.card_view_add_messages);
        mImgSelectImages = (ImageView) findViewById(R.id.img_selected_images);

        mRecyclerViewImages = (RecyclerView) findViewById(R.id.rec_select_images);


        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImages.setLayoutManager(manager);
        mRecyclerViewImages.setHasFixedSize(true);

        mSelectedImageAdapter = new SelectedImageAdapter(this, mListImagesSelected);
        mRecyclerViewImages.setAdapter(mSelectedImageAdapter);

    }


    private void addEvents() {
        addMessage.setOnClickListener(new Events());
        mImgSelectImages.setOnClickListener(new Events());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.menu_add:
                onClickAddMessages();
                return true;
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(3, intent);
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void addNews(final String title, final String content, final String link) {

        String idNews = MainApplication.getRandomString(15);

        Message template = new Message();

        template.setMessage_id(idNews);
        template.setContent(content);
        template.setLink(link);
        template.setTitle(title);
        template.setCompany_id(SaveData.company.company_id + "");
        if (mLinkImageNews != null) {
            template.setImages_link(mLinkImageNews);
        }
        //template.created_date= new Date();

        Call<Message> call2 = apiService.addMessage(template);
        call2.enqueue(new Callback<Message>() {

            @Override
            public void onResponse(Call<Message> arg0, Response<Message> arg1) {

                getSnackBar(getString(R.string.add_message_success));
                mTxtTitle.setText("");
                mTxtContent.setText("");
                mTxtLink.setText("");
                mListImagesSelected.clear();
                mSelectedImageAdapter.notifyDataSetChanged();

                hideProgressDialog();
            }

            @Override
            public void onFailure(Call<Message> arg0, Throwable arg1) {

                getSnackBar(getString(R.string.add_message_fail));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            List<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);

            for (String s : images) {
                if (!isExists(s)) {
                    mListStrImages.add(s);
                    ItemImage itemImage = new ItemImage(createFile(s));
                    mListImagesSelected.add(itemImage);
                    mIsShowRecyclerView = true;
                } else {
                    getSnackBar(getString(R.string.images_chose_is_exists));
                }
            }

            if (mIsShowRecyclerView) {
                mRecyclerViewImages.setVisibility(View.VISIBLE);
            }

            mSelectedImageAdapter.notifyDataSetChanged();
        }
    }

    private boolean isExists(String s) {
        for (String itemImage : mListStrImages) {
            if (itemImage.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Đang tạo message");
            mProgressDialog.setMessage("Running...");
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private class Events implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.card_view_add_messages:
                    onClickAddMessages();
                    break;
                case R.id.img_selected_images:
                    onClickSelectImages();
                    break;
            }
        }

        private void onClickSelectImages() {
            ImageSelectorActivity.start(AddMessageActivity.this, mSelectNumber, mMode, isShow, isPreview, isCrop);
        }
    }

    private void onClickAddMessages() {

        String str_title = mTxtTitle.getText().toString();
        String str_content = mTxtContent.getText().toString();
        String str_link = mTxtLink.getText().toString();
        if (str_title.length() > 0 && str_content.length() > 0 && str_link.length() > 0) {
            showProgressDialog();
            for (ItemImage itemImage : mListImagesSelected) {
                String str = itemImage.getPath().substring(itemImage.getPath().lastIndexOf("/") + 1);
                String url = MainApplication.URL_UPDATE_IMAGE + "/upload/" + str;
                if (mLinkImageNews != null && mLinkImageNews.length() > 0) {
                    mLinkImageNews += ";" + url;
                } else {
                    mLinkImageNews = url;
                }

                Log.d(TAG, "linkImages : " + url);
            }

            for (ItemImage itemImage : mListImagesSelected) {
                uploadFile(itemImage.getPath());
            }

            addNews(str_title, str_content, str_link);

        } else {
            getSnackBar(getString(R.string.not_fill_login));
        }
    }

//    private String convertBase64(String path) {
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//
//        int height = MainApplication.WIDTH_IMAGES * bitmap.getHeight() / bitmap.getWidth();
//        Bitmap resized = Bitmap.createScaledBitmap(bitmap, MainApplication.WIDTH_IMAGES, height, true);
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
//    }

    private String createFile(String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

//        int height = MainApplication.WIDTH_IMAGES * bitmap.getHeight() / bitmap.getWidth();
        int height = MainApplication.WIDTH_IMAGES;
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, height, height, true);

        String nameImages = path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());

        File resizedFile = new File(this.getCacheDir(), nameImages + "_" + timeStamp + ".png");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(resizedFile);
            fos.write(byteArray);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.toString());
        } catch (IOException e) {
            Log.d(TAG, e.toString());

        }

        return resizedFile.getAbsolutePath();
    }

    private void uploadFile(String path) {

        File file = new File(path);
        Log.d(TAG, "file name : " + file.getName());

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = mApiService.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                Log.d(TAG, "response" + response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void getSnackBar(String s) {
        Snackbar.make(layoutView, s, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
