package com.ln.fragment.shop;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.ln.api.SaveData;
import com.ln.app.MainApplication;
import com.ln.model.Company;
import com.ln.mycoupon.R;
import com.ln.views.CircleImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private static final String IMAGE_DIRECTORY_NAME = "MyCoupon";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int SELECT_PICTURE = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoveCouponAPI = MainApplication.getAPI();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(MainApplication.FILE_URI);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainApplication.FILE_URI, mFileUri);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        mLoveCouponAPI = MainApplication.getAPI();

        initViews(v);
        init();
        addEvents();
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

        Company company = SaveData.company;

        if (company.name != null) {
            mEdtNameCompany.setText(company.name);
            mTxtNameCompany.setText(company.getName());
        }

        if (company.address != null) {
            mEdtAddress.setText(company.address);
            mTxtAddress.setText(company.getAddress());
        }

        if (company.getLogo() != null) {
            String logo = company.getLogo();

            Glide.with(getActivity()).load(MainApplication.convertToBytes(logo))
                    .asBitmap()
                    .placeholder(R.drawable.ic_profile)
                    .into(mImgLogo);
        }


        if (company.user1 != null) {
            mEdtUser1.setText(company.user1);
        }

        if (company.user2 != null) {
            mEdtUser2.setText(company.user2);
        }

        if (company.pass1 != null) {
            mEdtPassword1.setText(company.pass1);
        }

        if (company.pass2 != null) {
            mEdtPassword2.setText(company.pass2);
        }

        if (company.user1_admin != null) {
            if (company.user1_admin.equals("1")) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

        if (company.user2_admin != null) {
            if (company.user2_admin.equals("1")) {
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
            previewCapturedImage(data.getData().getPath());
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            Log.d(TAG, mFileUri.getPath());
            previewCapturedImage(mFileUri.getPath());
        } else {
            getShowMessage("User cancelled image capture");
        }

    }

    private boolean isDriverSupportCamera() {
        return getActivity().getApplicationContext().getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("SettingFragment", "Oops! Failed create " + IMAGE_DIRECTORY_NAME);
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss",
                Locale.getDefault()).format(new Date());

        return new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
    }


    private void previewCapturedImage(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            mImgLogo.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
                case R.id.img_logo_company:
                    onClickChangeLogo(mImgLogo);
                    break;
                case R.id.fab_done:
                    onClickSaveCompany();
                    break;
                default:
                    break;
            }
        }

        private void onClickOpenCamera() {
            if (isDriverSupportCamera()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mFileUri = getOutputMediaFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            } else {
                getShowMessage("Driver do not Support");
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


        private void onClickChangeLogo(View view) {

            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            final MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.menu_chose_images, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_camera:
                            onClickOpenCamera();
                            break;
                        case R.id.menu_gallery:
                        default:
                            onClickOpenGallery();
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();

        }

        private void onClickSaveCompany() {

            Company company = SaveData.company;

            company.setName(mEdtNameCompany.getText().toString());
            company.setAddress(mEdtNameCompany.getText().toString());

            String logo = MainApplication.convertToBitmap(mImgLogo);
            logo = MainApplication.FIRST_BASE64 + logo;

            company.setLogo(logo);
            Call<Company> call = mLoveCouponAPI.updateCompany(company);
            call.enqueue(new Callback<Company>() {
                @Override
                public void onResponse(Call<Company> call, Response<Company> response) {
                    Log.d(TAG, "Success");
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

        validateUser(mEdtUser1, mEdtUser2, mInputUser1, mInputPassword1);
    }

    private void validateUser(EditText editText, EditText editText2, TextInputLayout textInputLayout, TextInputLayout password) {

        Company company = SaveData.company;

        String text = editText.getText().toString().trim();
        String text2 = editText2.getText().toString().trim();
        if (text.equals(text2)) {
            textInputLayout.setError(getString(R.string.user1OtherUser2));
            requestFocus(editText);
        } else if (isExistsAccount1(company.getUser_id(), text) == 1) {
            textInputLayout.setError(getString(R.string.account_exists));
            requestFocus(editText);
        } else {
            textInputLayout.setErrorEnabled(false);
            if (!text.isEmpty()) {
                password.setError(getString(R.string.enter_password));
            }
        }

    }

    private void requestFocus(View view) {

        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void onClickUser2() {

        validateUser(mEdtUser2, mEdtUser1, mInputUser2, mInputPassword2);
    }

    private void getShowMessage(String s) {
        Snackbar.make(mLinearLayout, s, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }

}
