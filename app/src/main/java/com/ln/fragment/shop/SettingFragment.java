package com.ln.fragment.shop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.isseiaoki.simplecropview.util.Utils;
import com.ln.app.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.images.activities.ImagesCropActivity;
import com.ln.model.Company;
import com.ln.mycoupon.R;
import com.ln.views.CircleImageView;
import com.ln.views.MaterialEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final int START_CROP_IMAGES = 99;
    private static final int IS_CHECK_FOCUS = 999;
    private final String TAG = getClass().getSimpleName();

    private LoveCouponAPI mLoveCouponAPI;

    private MaterialEditText mEdtNameCompany, mEdtAddress,
            mEdtPassword1, mEdtUser1, mEdtUser2, mEdtPassword2;
    private CheckBox checkBox, checkBox1;
    private CardView mCardView;
    private CircleImageView mImgLogo;
    private TextView mTxtNameCompany, mTxtAddress;
    private CheckBox mChbShowPass;
    private FloatingActionButton mFabDoneSave;

    private static final int SELECT_PICTURE = 100;

    private Company company;
    private static OnClickSetInformation mListener;

    private ProgressDialog mProgressDialog;
    private boolean isChoseImages;
    private String mLogoBase64;

    private ExecutorService mExecutor;

    private Uri mUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoveCouponAPI = MainApplication.getAPI();
        mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.submit(new LoadScaledImageTask(getActivity(), mUri, mImgLogo, calcImageSize()));

        String strCompany = MainApplication
                .getPreferences()
                .getString(MainApplication.COMPANY_SHOP, "");

        company = new Gson().fromJson(strCompany, Company.class);
    }

    public static void setListener(OnClickSetInformation listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        mLoveCouponAPI = MainApplication.getAPI();

        initViews(v);
        init();
        addEvents();
        setHasOptionsMenu(false);


        (v.findViewById(R.id.text_no_data)).setVisibility(View.GONE);

        if (!MainApplication.getPreferences().getBoolean(MainApplication.ADMIN, false)) {
            (v.findViewById(R.id.linear_information)).setVisibility(View.GONE);
            mFabDoneSave.setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.text_no_data)).setText(R.string.only_admin_edit);
            (v.findViewById(R.id.text_no_data)).setVisibility(View.VISIBLE);
        }

        return v;
    }


    private void initViews(View v) {

        mFabDoneSave = (FloatingActionButton) v.findViewById(R.id.fab_done);
        mChbShowPass = (CheckBox) v.findViewById(R.id.chb_show_password);


        mEdtNameCompany = (MaterialEditText) v.findViewById(R.id.name_company);
        mEdtAddress = (MaterialEditText) v.findViewById(R.id.address_company);

        mEdtUser1 = (MaterialEditText) v.findViewById(R.id.username1);
        mEdtPassword1 = (MaterialEditText) v.findViewById(R.id.password1);

        mEdtUser2 = (MaterialEditText) v.findViewById(R.id.username2);
        mEdtPassword2 = (MaterialEditText) v.findViewById(R.id.password2);

        checkBox = (CheckBox) v.findViewById(R.id.check_admin1);
        checkBox1 = (CheckBox) v.findViewById(R.id.check_admin2);

        mCardView = (CardView) v.findViewById(R.id.cardview1);
        mImgLogo = (CircleImageView) v.findViewById(R.id.img_logo_nav);
        mTxtNameCompany = (TextView) v.findViewById(R.id.txt_name_nav);
        mTxtAddress = (TextView) v.findViewById(R.id.txt_email_nav);
    }


    private void init() {

        if (company.getName() != null) {
            mEdtNameCompany.setText(company.getName());
            mTxtNameCompany.setText(company.getName());
        } else {
            mEdtNameCompany.setText("");
            mTxtNameCompany.setText("");
        }

        if (company.getAddress() != null) {
            mEdtAddress.setText(company.getAddress());
            mTxtAddress.setText(company.getAddress());
        } else {
            mEdtAddress.setText("");
            mTxtAddress.setText("");
        }

        if (company.getLogo() != null) {
            String logo = company.getLogo();

            Glide.with(getActivity()).load(MainApplication.convertToBytes(logo))
                    .asBitmap()
                    .placeholder(R.drawable.ic_logo_blank)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(mImgLogo);
        }

        if (company.getUser1() != null) {
            mEdtUser1.setText(company.getUser1());
        } else {
            mEdtUser1.setText("");
        }

        if (company.getUser2() != null) {
            mEdtUser2.setText(company.getUser2());
        }

        if (company.getPass1() != null) {
            mEdtPassword1.setText(company.getPass1());
        } else {
            mEdtPassword1.setText("");
        }

        if (company.getPass2() != null) {
            mEdtPassword2.setText(company.getPass2());
        } else {
            mEdtPassword2.setText("");
        }

        checkBox.setChecked(false);
        if (company.getUser1_admin() != null) {
            if (company.getUser1_admin().equals("1")) {
                checkBox.setChecked(true);
            }
        }

        checkBox1.setChecked(false);
        if (company.getUser2_admin() != null) {
            if (company.getUser2_admin().equals("1")) {
                checkBox1.setChecked(true);
            }
        }
    }

    private void addEvents() {

        mCardView.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mEdtNameCompany.addTextChangedListener(new Events(mEdtNameCompany));
        mEdtAddress.addTextChangedListener(new Events(mEdtAddress));

        mFabDoneSave.setOnClickListener(this);
        mChbShowPass.setOnCheckedChangeListener(this);

        mEdtUser1.addTextChangedListener(new Events(mEdtUser1));
        mEdtUser2.addTextChangedListener(new Events(mEdtUser2));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == START_CROP_IMAGES) {

                mUri = data.getData();
                mExecutor.submit(new LoadScaledImageTask(getActivity(), mUri, mImgLogo, calcImageSize()));
                Log.d(TAG, "data");
                isChoseImages = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardview1:
                onClickSaveCompany();
                break;
            case R.id.img_logo_nav:
                startActivityForResult(new Intent(getActivity(),
                        ImagesCropActivity.class), START_CROP_IMAGES);
                break;
            case R.id.fab_done:
                onClickSaveCompany();
                break;
            default:
                break;
        }
    }


    private void onClickSaveCompany() {

        boolean isNetwork = ConnectivityReceiver.isConnect();
        if (!isNetwork) {
            showMessage(getString(R.string.check_network));
            return;
        }

        checkAccount1(true);

    }

    private void save() {
        showProgressDialog();
        final String name = mEdtNameCompany.getText().toString();
        final String address = mEdtAddress.getText().toString();

        mLogoBase64 = MainApplication.FIRST_BASE64
                + MainApplication.convertToBitmap(mImgLogo);


        final String user1 = mEdtUser1.getText().toString().trim();
        final String user2 = mEdtUser2.getText().toString().trim();


        company.setName(name);
        company.setAddress(address);
        if (isChoseImages) {
            company.setLogo(mLogoBase64);
        }

        company.setUser1(user1);
        company.setUser2(user2);
        company.setPass1(mEdtPassword1.getText().toString().trim());
        company.setPass2(mEdtPassword2.getText().toString().trim());

        company.setUser1_admin("");
        if (checkBox.isChecked()) {
            company.setUser1_admin("1");
        }

        company.setUser2_admin("");
        if (checkBox1.isChecked()) {
            company.setUser2_admin("1");
        }

        createSave();
    }

    private void checkAccount1(final boolean isEnd) {


        String user = mEdtUser1.getText().toString().trim();
        if (user.isEmpty()) {
            checkAccount2(isEnd);
            return;
        }
        Call<Integer> call = mLoveCouponAPI.isExists(company.getCompany_id(), user);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.body() == 1) {
                    if (isEnd) {
                        checkAccount2(isEnd);
                    }
                } else {
                    mEdtUser1.setError(getString(R.string.check_account));
                    requestFocus(mEdtUser1);
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, "isCheckAccountExists " + t.toString());
            }
        });
    }

    private void checkAccount2(final boolean isEnd) {

        String user = mEdtUser2.getText().toString().trim();
        if (user.isEmpty()) {
            save();
            return;
        }
        Call<Integer> call = mLoveCouponAPI.isExists(company.getCompany_id(), user);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.body() == 1) {
                    if (isEnd) {
                        save();
                    }
                } else {
                    mEdtUser2.setError(getString(R.string.check_account));
                    requestFocus(mEdtUser2);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, "isCheckAccountExists " + t.toString());
            }
        });
    }

    private void createSave() {

        Log.d(TAG, "user1: " + company.getUser1());
        Log.d(TAG, "user2: " + company.getUser2());
        Call<Integer> call3 = mLoveCouponAPI.updateCompany(company.getWeb_token(), company);
        call3.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    Log.d(TAG, "Success");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            showMessage(R.string.add_coupon_success);

                            String str = new Gson().toJson(company);
                            writeSharePreferences(MainApplication.COMPANY_SHOP, str);

                            String strCompany = MainApplication
                                    .getPreferences()
                                    .getString(MainApplication.COMPANY_SHOP, "");

                            Company company1 = new Gson().fromJson(strCompany, Company.class);

                            Log.d(TAG, "user11: " + company1.getUser1());
                            Log.d(TAG, "user12: " + company1.getUser2());

                            if (mListener != null) {
                                mListener.onClickSetInformation(company.getLogo(), company.getName(), company.getAddress());
                            }
                            isChoseImages = false;
                            hideProgressDialog();
                        }
                    }, MainApplication.TIME_SLEEP_SETTING);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, "Fails " + t.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        showMessage("Save do not Success");
                        hideProgressDialog();
                    }
                }, MainApplication.TIME_SLEEP);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mEdtPassword1.setTransformationMethod(null);
            mEdtPassword2.setTransformationMethod(null);
        } else {
            mEdtPassword1.setTransformationMethod(new PasswordTransformationMethod());
            mEdtPassword2.setTransformationMethod(new PasswordTransformationMethod());
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.save_running));
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private class Events implements TextWatcher {

        private View view;

        Events(View editText) {
            this.view = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }


        @Override
        public void afterTextChanged(Editable editable) {

            switch (view.getId()) {
                case R.id.username1:
                    validateUser(mEdtUser1, mEdtUser2);
                    break;
                case R.id.username2:
                    validateUser(mEdtUser2, mEdtUser1);
                    break;
                case R.id.name_company:
                    mTxtNameCompany.setText(editable.toString());
                    break;
                case R.id.address_company:
                    mTxtAddress.setText(editable.toString());
                    break;
                default:
                    break;
            }
        }

    }

    private void validateUser(MaterialEditText editText, MaterialEditText editText2) {

        String text = editText.getText().toString().trim();
        String text2 = editText2.getText().toString().trim();

        if (text.equals(text2) && !text2.isEmpty()) {
            editText.setError(getString(R.string.check_account));
            requestFocus(editText);
        }
    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager
                    .LayoutParams
                    .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void writeSharePreferences(String key, String value) {
        SharedPreferences.Editor editor = MainApplication.getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void showMessage(int s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public static class LoadScaledImageTask implements Runnable {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        private Context context;
        Uri uri;
        ImageView imageView;
        int width;

        LoadScaledImageTask(Context context, Uri uri, ImageView imageView, int width) {
            this.context = context;
            this.uri = uri;
            this.imageView = imageView;
            this.width = width;
        }

        @Override
        public void run() {
            final int exifRotation = Utils.getExifOrientation(context, uri);
            int maxSize = Utils.getMaxSize();
            int requestSize = Math.min(width, maxSize);
            try {
                final Bitmap sampledBitmap = Utils.decodeSampledBitmapFromUri(context, uri, requestSize);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageMatrix(Utils.getMatrixFromExifOrientation(exifRotation));
                        imageView.setImageBitmap(sampledBitmap);
                    }
                });
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }

    public int calcImageSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return Math.min(Math.max(metrics.widthPixels, metrics.heightPixels), 2048);
    }

    public interface OnClickSetInformation {
        void onClickSetInformation(String logo, String name, String address);
    }
}
