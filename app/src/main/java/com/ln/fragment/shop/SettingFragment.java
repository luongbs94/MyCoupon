package com.ln.fragment.shop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.isseiaoki.simplecropview.util.Utils;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.broadcast.ConnectivityReceiver;
import com.ln.images.activities.ImagesCropActivity;
import com.ln.interfaces.OnClickSetInformation;
import com.ln.model.Company;
import com.ln.mycoupon.R;
import com.ln.views.CircleImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/14/16.
 * setting account shop
 */
public class SettingFragment extends Fragment implements
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static final int START_CROP_IMAGES = 99;
    private LoveCouponAPI mLoveCouponAPI;

    private final String TAG = getClass().getSimpleName();

    private TextInputLayout mInputUser1, mInputUser2, mInputPassword1, mInputPassword2;
    private EditText mEdtNameCompany, mEdtAddress, mEdtPassword1, mEdtUser2, mEdtPassword2;
    private CheckBox checkBox, checkBox1;
    private EditText mEdtUser1;
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

        return v;
    }


    private void initViews(View v) {

        mFabDoneSave = (FloatingActionButton) v.findViewById(R.id.fab_done);
        mChbShowPass = (CheckBox) v.findViewById(R.id.chb_show_password);


        if (!MainApplication.sIsAdmin) {
            (v.findViewById(R.id.linear_information)).setVisibility(View.GONE);
            mFabDoneSave.setVisibility(View.GONE);
        }

        mEdtNameCompany = (EditText) v.findViewById(R.id.name_company);
        mEdtAddress = (EditText) v.findViewById(R.id.address_company);
        mEdtUser1 = (EditText) v.findViewById(R.id.username1);
        mEdtPassword1 = (EditText) v.findViewById(R.id.password1);

        mEdtUser2 = (EditText) v.findViewById(R.id.username2);
        mEdtPassword2 = (EditText) v.findViewById(R.id.password2);

        checkBox = (CheckBox) v.findViewById(R.id.check_admin1);
        checkBox1 = (CheckBox) v.findViewById(R.id.check_admin2);

        mCardView = (CardView) v.findViewById(R.id.cardview1);
        mImgLogo = (CircleImageView) v.findViewById(R.id.img_logo_nav);
        mTxtNameCompany = (TextView) v.findViewById(R.id.txt_name_nav);
        mTxtAddress = (TextView) v.findViewById(R.id.txt_email_nav);

        mInputUser1 = (TextInputLayout) v.findViewById(R.id.input_user1);
        mInputUser2 = (TextInputLayout) v.findViewById(R.id.input_user2);
        mInputPassword1 = (TextInputLayout) v.findViewById(R.id.input_password1);
        mInputPassword2 = (TextInputLayout) v.findViewById(R.id.input_password2);
    }


    private void init() {

        if (company.getName() != null) {
            mEdtNameCompany.setText(company.getName());
            mTxtNameCompany.setText(company.getName());
        }

        if (company.getAddress() != null) {
            mEdtAddress.setText(company.getAddress());
            mTxtAddress.setText(company.getAddress());
        }

        if (company.getLogo() != null) {
            String logo = company.getLogo();

            Glide.with(getActivity()).load(MainApplication.convertToBytes(logo))
                    .asBitmap()
                    .placeholder(R.drawable.ic_logo_blank)
                    .into(mImgLogo);
        }

        if (company.getUser1() != null) {
            mEdtUser1.setText(company.getUser1());
        }

        if (company.getUser2() != null) {
            mEdtUser2.setText(company.getUser2());
        }

        if (company.getPass1() != null) {
            mEdtPassword1.setText(company.getPass1());
        }

        if (company.getPass2() != null) {
            mEdtPassword2.setText(company.getPass2());
        }

        if (company.getUser1_admin() != null) {
            if (company.getUser1_admin().equals("1")) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

        if (company.getUser2_admin() != null) {
            if (company.getUser2_admin().equals("1")) {
                checkBox1.setChecked(true);
            } else {
                checkBox1.setChecked(false);
            }
        }
    }

    private void addEvents() {

        mCardView.setOnClickListener(this);
        mImgLogo.setOnClickListener(this);
        mEdtNameCompany.addTextChangedListener(new Events(mEdtNameCompany, null));
        mEdtAddress.addTextChangedListener(new Events(mEdtAddress, null));

        mFabDoneSave.setOnClickListener(this);
        mChbShowPass.setOnCheckedChangeListener(this);

        mEdtUser1.addTextChangedListener(new Events(mEdtUser1, mInputUser1));
        mEdtUser2.addTextChangedListener(new Events(mEdtUser2, mInputUser2));

        mEdtPassword1.addTextChangedListener(new Events(mEdtPassword1, mInputPassword1));
        mEdtPassword2.addTextChangedListener(new Events(mEdtPassword2, mInputPassword2));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "requestCode = " + requestCode
                + " - resultCode " + resultCode
                + " + " + Activity.RESULT_OK);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == START_CROP_IMAGES) {

                mUri = data.getData();
                mExecutor.submit(new LoadScaledImageTask(getActivity(), mUri, mImgLogo, calcImageSize()));
                Log.d(TAG, "data");
                isChoseImages = true;
            }
        } else {
            getShowMessage("User cancelled image capture");
        }
    }

    private boolean isDriverSupportCamera() {

        return getActivity()
                .getApplicationContext()
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);
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
        if (isNetwork) {
            showProgressDialog();
            final String name = mEdtNameCompany.getText().toString();
            final String address = mEdtAddress.getText().toString();

            mLogoBase64 = MainApplication.FIRST_BASE64
                    + MainApplication.convertToBitmap(mImgLogo);


            final String user1 = mEdtUser1.getText().toString().trim();
            final String user2 = mEdtUser2.getText().toString().trim();


            final Company companyTemplate = new Company(company.getCompany_id(),
                    company.getName(), company.getAddress(),
                    company.getLogo(), company.getCreated_date(),
                    company.getUser_id(), company.getUser1(),
                    company.getPass1(), company.getUser1_admin(),
                    company.getUser2(), company.getPass2(),
                    company.getUser2_admin(), company.getLogo_link(),
                    company.getCity(), company.getCountry_name());

            companyTemplate.setName(name);
            companyTemplate.setAddress(address);
            companyTemplate.setLogo(null);
            if (isChoseImages) {
                companyTemplate.setLogo(mLogoBase64);
            }
            companyTemplate.setUser1(user1);
            companyTemplate.setUser2(user2);


            Call<Integer> call = mLoveCouponAPI.isExists(company.getCompany_id(), user1);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {

                    if (response.body() == 1) {
                        mInputUser1.setErrorEnabled(false);

                        Call<Integer> call2 = mLoveCouponAPI.isExists(company.getCompany_id(), user2);
                        call2.enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {

                                if (response.body() == 1) {
                                    mInputUser2.setErrorEnabled(false);
                                    createSave(name, address, mLogoBase64, companyTemplate);

                                } else {
                                    mInputUser2.setError(getString(R.string.account_exists));
                                    mInputUser2.setErrorEnabled(true);
                                    mInputUser2.setError(getString(R.string.account_exists));
                                    requestFocus(mEdtUser2);

                                    hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Log.d(TAG, "isCheckAccountExists " + t.toString());
                            }
                        });


                    } else {
                        mInputUser1.setError(getString(R.string.account_exists));
                        mInputUser1.setErrorEnabled(true);
                        mInputUser1.setError(getString(R.string.account_exists));
                        requestFocus(mEdtUser1);

                        hideProgressDialog();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.d(TAG, "isCheckAccountExists " + t.toString());
                }
            });
        } else {
            getShowMessage(getString(R.string.check_network));
        }

    }

    private void createSave(final String name, final String address,
                            final String finalLogo, final Company companyTemplate) {
        Call<Integer> call3 = mLoveCouponAPI.updateCompany(companyTemplate);
        call3.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.body() == 1) {
                    Log.d(TAG, "Success");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            getShowMessage("Success");
                            company = companyTemplate;
                            company.setLogo(mLogoBase64);
                            String str = new Gson().toJson(company);
                            writeSharePreferences(MainApplication.COMPANY_SHOP, str);

                            if (mListener != null) {
                                mListener.onClickSetInformation(finalLogo, name, address);
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

                        getShowMessage("Save do not Success");
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
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private class Events implements TextWatcher {

        private View view;
        private TextInputLayout textInputLayout;

        public Events(View editText, TextInputLayout textInputLayout) {
            this.view = editText;
            this.textInputLayout = textInputLayout;
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
                    validateUser(mEdtUser1, mEdtUser2, mEdtPassword1, mInputUser1, mInputPassword1);
                    break;
                case R.id.username2:
                    validateUser(mEdtUser2, mEdtUser1, mEdtPassword2, mInputUser2, mInputPassword2);
                    break;
                case R.id.name_company:
                    mTxtNameCompany.setText(editable.toString());
                    break;
                case R.id.address_company:
                    mTxtAddress.setText(editable.toString());
                    break;
                case R.id.password1:
                    validatePassword(mEdtUser1, (EditText) view, textInputLayout);
                    break;
                case R.id.password2:
                    validatePassword(mEdtUser2, (EditText) view, textInputLayout);
                    break;
                default:
                    break;
            }
        }

    }

    private void validateUser(EditText editText,
                              EditText editText2, EditText edtPassword,
                              TextInputLayout textInputLayout,
                              TextInputLayout password) {

        String text = editText.getText().toString().trim();
        String text2 = editText2.getText().toString().trim();

        if (text.equals(text2)) {
            textInputLayout.setError(getString(R.string.user1OtherUser2));
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(getString(R.string.user1OtherUser2));
            requestFocus(editText);
        }

        if (!text.equals(text2)) {
            textInputLayout.setErrorEnabled(false);
        }
        if (!text.isEmpty() && edtPassword.getText().toString().trim().isEmpty()) {
            password.setErrorEnabled(true);
            password.setError(getString(R.string.enter_password));
            Log.d(TAG, "1");
        } else {
            password.setErrorEnabled(false);
        }
    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            getActivity()
                    .getWindow()
                    .setSoftInputMode(WindowManager
                            .LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void validatePassword(EditText user, EditText password, TextInputLayout inputPassword) {
        if (!user.getText().toString().trim().isEmpty() && password.getText().toString().trim().isEmpty()) {
            inputPassword.setErrorEnabled(true);
            inputPassword.setError(getString(R.string.enter_password));
            requestFocus(password);
        } else {
            inputPassword.setErrorEnabled(false);
        }
    }


    private void writeSharePreferences(String key, String value) {
        SharedPreferences.Editor editor = MainApplication.getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void getShowMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public static class LoadScaledImageTask implements Runnable {
        private Handler mHandler = new Handler(Looper.getMainLooper());
        private Context context;
        Uri uri;
        ImageView imageView;
        int width;

        public LoadScaledImageTask(Context context, Uri uri, ImageView imageView, int width) {
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
            } catch (Exception e) {
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
}
