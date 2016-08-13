package com.ln.mycoupon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.datetimepicker.date.DatePickerDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ln.adapter.SelectedImageAdapter;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.images.activities.ImagesCheckActivity;
import com.ln.images.models.LocalMedia;
import com.ln.model.Company;
import com.ln.model.NewsOfCompany;
import com.ln.views.MaterialEditText;
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

public class AddMessageActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
        View.OnClickListener, SelectedImageAdapter.OnClickRemoveImages {


    private static final int REQUEST_IMAGE = 77;
    private static final int CREATE_NEWS = 76;
    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI mApiService;
    private LoveCouponAPI apiService;

    private MaterialEditText mTxtTitle, mTxtContent, mTxtLink;
    private RecyclerView mRecyclerViewImages;


    private List<LocalMedia> mListLocalImages;
    private List<String> mListStringSelectImages;
    private SelectedImageAdapter mSelectedImageAdapter;

    private boolean mIsShowRecyclerView;
    private String mLinkImageNews;
    private ProgressDialog mProgressDialog;

    private TextView lastDate;

    private long mTimeLong;
    private Calendar mCalendar;

    private int mType = 0;
    private NewsOfCompany mNewsOfCompany;
    private boolean isEnd = false;

    private Handler mHandler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (!isEnd) {
                SystemClock.sleep(100);
            }

            Message message = new Message();
            message.what = CREATE_NEWS;
            message.setTarget(mHandler);
            message.sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        initData();
        initViews();
        addEvents();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == CREATE_NEWS) {
                    String title = mTxtTitle.getText().toString();
                    String content = mTxtContent.getText().toString();
                    String link = mTxtLink.getText().toString();
                    addNews(title, content, link);
                    isEnd = false;
                }
            }
        };
    }

    private void initData() {

        mListLocalImages = new ArrayList<>();
        mListStringSelectImages = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mType = bundle.getInt(MainApplication.WHAT_ADD_MESSAGES);
            if (mType == MainApplication.WHAT_UPDATE_NEWS) {
                String idNews = bundle.getString(MainApplication.DATA);
                NewsOfCompany news = MainApplication.mRealmController.getNewsOfCompanyById(idNews);
                try {
                    mNewsOfCompany = new NewsOfCompany(news.getMessage_id(),
                            news.getContent(), news.getCompany_id(),
                            news.getLast_date(), news.getTitle(),
                            news.getLink(), news.getImages_link());

                    if (news.getImages_link() != null) {
                        String imagesLink = news.getImages_link();
                        String[] strImage = imagesLink.split(";");
                        for (String path : strImage) {
                            mListLocalImages.add(new LocalMedia(path));
                            mListStringSelectImages.add(path);

                            Logger.d(path);
                        }

                        mIsShowRecyclerView = true;
                        findViewById(R.id.rec_select_images).setVisibility(View.VISIBLE);
                    }

                } catch (NullPointerException e) {
                    Logger.d(e.toString());
                }

            }
        }
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

        mSelectedImageAdapter = new SelectedImageAdapter(this, mListLocalImages);
        mSelectedImageAdapter.setOnClickRemoveImages(this);
        mRecyclerViewImages.setAdapter(mSelectedImageAdapter);

        if (mNewsOfCompany != null) {
            if (mNewsOfCompany.getTitle() != null) {
                mTxtTitle.setText(mNewsOfCompany.getTitle());
            }
            if (mNewsOfCompany.getContent() != null) {
                mTxtContent.setText(mNewsOfCompany.getContent());
            }
            if (mNewsOfCompany.getLink() != null) {
                mTxtLink.setText(mNewsOfCompany.getLink());
            }

            if (mNewsOfCompany.getLast_date() != 0) {
                SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                if (!MainApplication.isEnglish()) {
                    mDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                }

                lastDate.setText(mDateFormat.format(mNewsOfCompany.getLast_date()));
                mCalendar.setTimeInMillis(mNewsOfCompany.getLast_date());
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        mCalendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        if (!MainApplication.isEnglish()) {
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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent();
            setResult(3, intent);
            finish();
            return true;
        }
        return super.onContextItemSelected(item);

    }

    private void addNews(final String title, final String content, final String link) {


        final String strCompany = MainApplication.getPreferences().getString(MainApplication.COMPANY_SHOP, "");
        Company mCompany = new Gson().fromJson(strCompany, Company.class);

        String idCompany = mCompany.getCompany_id();
        final NewsOfCompany news = new NewsOfCompany();
        String token = mCompany.getWeb_token();
        news.setContent(content);
        news.setLink(link);
        if (mNewsOfCompany != null) {
            news.setMessage_id(mNewsOfCompany.getMessage_id());
        }
        news.setTitle(title);
        news.setCreated_date(System.currentTimeMillis());
        news.setLast_date(mTimeLong);
        Log.d(TAG, "Time  = " + mTimeLong);
        news.setCompany_id(idCompany);

        if (mLinkImageNews != null) {
            news.setImages_link(mLinkImageNews);
        }

        if (mType == MainApplication.WHAT_UPDATE_NEWS) {
            Call<Integer> update = apiService.updateMessages(token, news);
            update.enqueue(new Callback<Integer>() {
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
                                mType = 0;
                            }
                        }, MainApplication.TIME_SLEEP_SETTING);
                    } else {
                        getShowMessages(getString(R.string.add_message_fail));
                        hideProgressDialog();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    getShowMessages(getString(R.string.add_message_fail));
                    Log.d(TAG, "update " + t.toString());
                }
            });

        } else {

            String idNews = MainApplication.getRandomString(MainApplication.SIZE_ID);
            news.setMessage_id(idNews);


            Call<Integer> addNews = apiService.addMessage(token, news);
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
                        }, MainApplication.TIME_SLEEP_SETTING);
                    } else {
                        getShowMessages(getString(R.string.add_message_fail));
                        hideProgressDialog();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    getShowMessages(getString(R.string.add_message_fail));
                    Log.d(TAG, "addNews " + t.toString());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE) {
            List<String> images = Parcels.unwrap(data.getExtras().getParcelable(MainApplication.LIST_IMAGES));

            for (String s : images) {
                if (!isExists(s)) {
                    mListStringSelectImages.add(s);
                    mListLocalImages.add(new LocalMedia(createFile(s)));
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
        for (String itemImage : mListStringSelectImages) {
            if (itemImage.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.up_loading));
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
                if (!ConnectivityReceiver.isConnect()) {
                    getShowMessages(getString(R.string.check_network));
                    return;
                }
                onClickAddMessages();
                break;
            case R.id.img_selected_images:
                startActivityForResult(new Intent(this, ImagesCheckActivity.class), REQUEST_IMAGE);
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

    private void onClickAddMessages() {

        String title = mTxtTitle.getText().toString();
        String content = mTxtContent.getText().toString();
        String link = mTxtLink.getText().toString();
        if (title.length() > 0 && content.length() > 0) {
            showProgressDialog();
            for (LocalMedia itemImage : mListLocalImages) {
                String url;
                if (itemImage.getPath().contains("http")) {
                    url = itemImage.getPath();
                } else {
                    String str = itemImage.getPath().substring(itemImage.getPath().lastIndexOf("/") + 1);
                    url = MainApplication.URL_UPDATE_IMAGE + "/upload/" + str;
                }

                if (mLinkImageNews != null && mLinkImageNews.length() > 0) {
                    mLinkImageNews += ";" + url;
                } else {
                    mLinkImageNews = url;
                }

                Log.d(TAG, "linkImages : " + url);
            }

            int size = mListLocalImages.size();
            for (int i = 0; i < size; i++) {
                if (!mListLocalImages.get(i).getPath().contains("http")) {
                    if (i != size - 1) {
                        uploadFile(mListLocalImages.get(i).getPath(), false);
                    } else {
                        uploadFile(mListLocalImages.get(i).getPath(), true);
                        new Thread(runnable).start();
                    }
                }
            }

//            for (LocalMedia itemImage : mListLocalImages) {
//                if (!itemImage.getPath().contains("http")) {
//                    uploadFile(itemImage.getPath());
//                }
//            }

        } else {
            getShowMessages(getString(R.string.not_fill_login));
        }
    }


    private String createFile(String path) {

        Bitmap bitmap = scaleImages(path, MainApplication.WIDTH_IMAGES_NEWS);

        String nameImages = path.substring(path.lastIndexOf("/") + 1, path.indexOf("."));

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());

        File resizedFile = new File(this.getCacheDir(), nameImages + "_" + timeStamp + ".png");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(resizedFile);
            fos.write(byteArray);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }

        return resizedFile.getAbsolutePath();
    }

    private void uploadFile(String path, final boolean isEnd) {

        final File file = new File(path);
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
                AddMessageActivity.this.isEnd = isEnd;
                String url = MainApplication.URL_UPDATE_IMAGE + "/upload/" + file.getName();
                Glide.with(MainApplication.getInstance())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .preload();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public Bitmap scaleImages(String path, int width) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int bWidth = bitmap.getWidth();

        if (bWidth > width) {
            float ratioX = width / (float) bitmap.getWidth();
            int height = (int) (ratioX * bitmap.getHeight());
            Bitmap scaledBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            float middleX = width / 2.0f;
            float middleY = height / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioX, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            return scaledBitmap;
        }
        return bitmap;
    }

    private void writePreferences(String key, String value) {
        SharedPreferences.Editor editor = MainApplication.getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void getShowMessages(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void remove(int position) {
        mListStringSelectImages.remove(position);
    }

}
