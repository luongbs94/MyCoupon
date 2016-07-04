package com.ln.fragment.shop;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.api.LoveCouponAPI;
import com.ln.app.MainApplication;
import com.ln.interfaces.OnClickSetInformation;
import com.ln.model.Company;
import com.ln.mycoupon.R;
import com.ln.views.CircleImageView;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/14/16.
 * setting account shop
 */
public class SettingFragment extends Fragment {

    private LoveCouponAPI mLoveCouponAPI;

    private String TAG = getClass().getSimpleName();

    private TextInputLayout mInputUser1, mInputUser2, mInputPassword1, mInputPassword2;
    private EditText mEdtNameCompany, mEdtAddress, mEdtPassword1, mEdtUser2, mEdtPassword2;
    private CheckBox checkBox, checkBox1;
    private EditText mEdtUser1;
    private CardView mCardView;
    private CircleImageView mImgLogo;
    private TextView mTxtNameCompany, mTxtAddress;
    private CheckBox mChbShowPass;
    private LinearLayout mLinearLayout;
    private FloatingActionButton mFabDoneSave;

    private Uri mFileUri;

    private static final int SELECT_PICTURE = 1;

    private static OnClickSetInformation mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoveCouponAPI = MainApplication.getAPI();
    }

    public static void setListener(OnClickSetInformation listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        mLinearLayout = (LinearLayout) v.findViewById(R.id.linear_information);

        if (!MainApplication.sIsAdmin) {
            mLinearLayout.setVisibility(View.GONE);
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

        Company company = MainApplication.mRealmController.getAccountShop();

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

        mCardView.setOnClickListener(new Events());
        mImgLogo.setOnClickListener(new Events());
        mEdtNameCompany.addTextChangedListener(new Events());
        mEdtAddress.addTextChangedListener(new Events());

        mFabDoneSave.setOnClickListener(new Events());
        mChbShowPass.setOnCheckedChangeListener(new Events());

        mEdtUser1.addTextChangedListener(new Events());
        mEdtUser2.addTextChangedListener(new Events());

        mEdtPassword1.addTextChangedListener(new Events());
        mEdtPassword2.addTextChangedListener(new Events());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SELECT_PICTURE) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);

                Bitmap resize = Bitmap.createScaledBitmap(bitmap, MainApplication.WIDTH_IMAGES,
                        MainApplication.WIDTH_IMAGES, true);
                mImgLogo.setImageBitmap(resize);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            getShowMessage("User cancelled image capture");
        }
    }

    private boolean isDriverSupportCamera() {
        return getActivity().getApplicationContext().getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private int isExistsAccount1(String company_id, String username) {

        final int[] result = new int[1];
        Call<Integer> call = mLoveCouponAPI.isExists(company_id, username);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                result[0] = response.body();
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d(TAG, "isExists " + t.toString());
            }
        });
        return result[0];
    }


    private class Events implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cardview1:
                    onClickSaveCompany();
                    break;
                case R.id.img_logo_nav:
                    onClickOpenGallery();
                    break;
                case R.id.fab_done:
                    onClickSaveCompany();
                    break;
                default:
                    break;
            }
        }

        private void onClickOpenGallery() {
            if (isDriverSupportCamera()) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_PICTURE);
            } else {
                getShowMessage("Driver do not Support");
            }
        }


        private void onClickSaveCompany() {

            final String name = mEdtNameCompany.getText().toString();
            final String address = mEdtNameCompany.getText().toString();
            String logo = MainApplication.convertToBitmap(mImgLogo);
            logo = MainApplication.FIRST_BASE64 + logo;

            Company company = MainApplication.mRealmController.getAccountShop();

            company.setName(name);
            company.setAddress(address);
            company.setLogo(logo);

            Call<Company> call = mLoveCouponAPI.updateCompany(company);
            final String finalLogo = logo;
            call.enqueue(new Callback<Company>() {
                @Override
                public void onResponse(Call<Company> call, Response<Company> response) {
                    Log.d(TAG, "Success");
                    getShowMessage("Success");
                    if (mListener != null) {
                        mListener.onClickSetInformation(finalLogo, name, address);
                    }

                }

                @Override
                public void onFailure(Call<Company> call, Throwable t) {
                    Log.d(TAG, "Fails");
                }
            });

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (mEdtNameCompany.isFocused()) {
                mTxtNameCompany.setText(editable.toString());
            } else if (mEdtAddress.isFocused()) {
                mTxtAddress.setText(editable.toString());
            } else if (mEdtUser1.isFocused()) {
                onClickUser1();
            } else if (mEdtUser2.isFocused()) {
                onClickUser2();
            } else if (mEdtPassword1.isFocused()) {
                onClickPassword1();
            } else if (mEdtPassword2.isFocused()) {
                onClickPassword2();
            }
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
    }

    private void onClickUser1() {

        validateUser(mEdtUser1, mEdtUser2, mEdtPassword1, mInputUser1, mInputPassword1);
    }

    private void onClickUser2() {
        validateUser(mEdtUser2, mEdtUser1, mEdtPassword2, mInputUser2, mInputPassword2);
    }

    private void validateUser(EditText editText, EditText editText2, EditText edtPassword, TextInputLayout textInputLayout, TextInputLayout password) {

        Company company = MainApplication.mRealmController.getAccountShop();

        String text = editText.getText().toString().trim();
        String text2 = editText2.getText().toString().trim();
        if (text.equals(text2)) {
            textInputLayout.setError(getString(R.string.user1OtherUser2));
            requestFocus(editText);
        } else if (isExistsAccount1(company.getUser_id(), text) == 0) {
            textInputLayout.setError(getString(R.string.account_exists));
            requestFocus(editText);
        } else {
            textInputLayout.setErrorEnabled(false);
            if (!text.isEmpty() && edtPassword.getText().toString().trim().isEmpty()) {
                password.setErrorEnabled(true);
                password.setError(getString(R.string.enter_password));
            } else {
                password.setErrorEnabled(false);
            }
        }

    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void onClickPassword1() {
        validatePassword(mEdtUser1, mEdtPassword1, mInputPassword1);
    }

    private void onClickPassword2() {
        validatePassword(mEdtUser2, mEdtPassword2, mInputPassword2);
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

    private void getShowMessage(String s) {
        Snackbar.make(mLinearLayout, s, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
}
