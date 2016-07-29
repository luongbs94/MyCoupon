package com.ln.mycoupon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.google.gson.Gson;
import com.ln.adapter.SelectedImageAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.images.activities.ImagesCheckActivity;
import com.ln.model.Company;
import com.ln.model.ItemImage;
import com.ln.model.NewsOfCompany;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class AddMessageActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static final int REQUEST_IMAGE = 77;
    private final String TAG = getClass().getSimpleName();

    private static final int mSelectNumber = 9;
    private static final int mMode = 1;
    private static final boolean isShow = true;
    private static final boolean isPreview = true;
    private static final boolean isCrop = false;

    private LoveCouponAPI mApiService;
    private LoveCouponAPI apiService;

    private MaterialEditText mTxtTitle, mTxtContent, mTxtLink;
    private RecyclerView mRecyclerViewImages;


    private List<ItemImage> mListImagesSelected = new ArrayList<>();
    private List<String> mListStrImages = new ArrayList<>();
    private SelectedImageAdapter mSelectedImageAdapter;

    private boolean mIsShowRecyclerView;
    private String mLinkImageNews;
    private ProgressDialog mProgressDialog;

    private TextView lastDate;

    private long mTimeLong;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        initViews();
        addEvents();
    }

    private void initViews() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiService = MainApplication.getAPI();
        mApiService = MainApplication.getAPI1();

        mCalendar = Calendar.getInstance();

        mTxtTitle = (MaterialEditText) findViewById(R.id.title);
        mTxtContent = (MaterialEditText) findViewById(R.id.content);
        mTxtLink = (MaterialEditText) findViewById(R.id.link);

        lastDate = (TextView) findViewById(R.id.date_add_message);


        mRecyclerViewImages = (RecyclerView) findViewById(R.id.rec_select_images);
        mRecyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewImages.setHasFixedSize(true);

        mSelectedImageAdapter = new SelectedImageAdapter(this, mListImagesSelected);
        mRecyclerViewImages.setAdapter(mSelectedImageAdapter);

    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        mCalendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        if (!MainApplication.getLanguage()) {
            mDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        }

        mTimeLong = mCalendar.getTimeInMillis();
        lastDate.setText(mDateFormat.format(mTimeLong));
    }


    private void addEvents() {
        findViewById(R.id.text_change_date).setOnClickListener(this);
        findViewById(R.id.card_view_add_messages).setOnClickListener(this);
        findViewById(R.id.img_selected_images).setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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

        String idNews = MainApplication.getRandomString(MainApplication.SIZE_ID);

        String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company mCompany = new Gson().fromJson(strCompany, Company.class);

        String idCompany = mCompany.getCompany_id();
        final NewsOfCompany news = new NewsOfCompany();

        news.setMessage_id(idNews);
        news.setContent(content);
        news.setLink(link);
        news.setTitle(title);
        news.setCreated_date(System.currentTimeMillis());
        news.setLast_date(mTimeLong);
        Log.d(TAG, "Time  = " + mTimeLong);
        news.setCompany_id(idCompany);

        if (mLinkImageNews != null) {
            news.setImages_link(mLinkImageNews);
        }

        Call<Integer> addNews = apiService.addMessage(news);
        addNews.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == MainApplication.SUCCESS) {

                    MainApplication.mRealmController.addNewsOfCompany(news);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK);
                            hideProgressDialog();
                            getShowMessages(getString(R.string.add_message_success));
                            finish();
                        }
                    }, MainApplication.TIME_SLEEP);
                } else {
                    getShowMessages(getString(R.string.add_message_fail));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                getShowMessages(getString(R.string.add_message_fail));
                Log.d(TAG, "addNews " + t.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            List<String> images = (ArrayList<String>) data.getSerializableExtra(MainApplication.LIST_IMAGES);

            for (String s : images) {
                if (!isExists(s)) {
                    mListStrImages.add(s);
                    ItemImage itemImage = new ItemImage(createFile(s));
                    mListImagesSelected.add(itemImage);
                    mIsShowRecyclerView = true;
                } else {
                    getShowMessages(getString(R.string.images_chose_is_exists));
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
            mProgressDialog.setMessage(getString(R.string.com_facebook_loading));
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_view_add_messages:
                onClickAddMessages();
                break;
            case R.id.img_selected_images:
                onClickSelectImages();
                break;
            case R.id.text_change_date:
                DatePickerDialog.newInstance(AddMessageActivity.this,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH))
                        .show(getFragmentManager(), "datePicker");
                break;
            default:
                break;
        }
    }


    private void onClickSelectImages() {
//        ImageSelectorActivity.start(AddMessageActivity.this, mSelectNumber, mMode, isShow, isPreview, isCrop);

        startActivityForResult(new Intent(this, ImagesCheckActivity.class), REQUEST_IMAGE);
    }

    private void onClickAddMessages() {

        String title = mTxtTitle.getText().toString();
        String content = mTxtContent.getText().toString();
        String link = mTxtLink.getText().toString();
        if (title.length() > 0 && content.length() > 0) {
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

            addNews(title, content, link);

        } else {
            getShowMessages(getString(R.string.not_fill_login));
        }
    }


    private String createFile(String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        int height = MainApplication.WIDTH_IMAGES * bitmap.getHeight() / bitmap.getWidth();
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, MainApplication.WIDTH_IMAGES, height, true);

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
            }
        });
    }

    private void getShowMessages(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
