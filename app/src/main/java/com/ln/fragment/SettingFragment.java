package com.ln.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Company;
import com.ln.model.Models;
import com.ln.model.UserPicture;
import com.ln.mycoupon.MainApplication;
import com.ln.mycoupon.R;
import com.ln.views.CircleImageView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by luongnguyen on 4/14/16.
 */
public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";
    private MaterialEditText nameCompany, addressCompany, user1, pass1, user2, pass2;
    private CheckBox checkBox, checkBox1;
    private CardView mCardView;
    private LoveCouponAPI mLoveCouponAPI;
    private CircleImageView mImgLogo;
    private TextView mTxtNameCompany, mTxtAddress;
    private TextView mTxtCamera, mTxtGallery;

    private FloatingActionButton mFbFinish;

    private Uri mFileUri;
    private Dialog mDialog;
    private Drawable mDrawable;
    private boolean isNameCompanry;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoveCouponAPI = MainApplication.getAPI();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(Models.FILE_URI);
        }
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
        nameCompany = (MaterialEditText) v.findViewById(R.id.name_company);
        addressCompany = (MaterialEditText) v.findViewById(R.id.adress_company);
        user1 = (MaterialEditText) v.findViewById(R.id.username1);
        pass1 = (MaterialEditText) v.findViewById(R.id.password1);
        pass2 = (MaterialEditText) v.findViewById(R.id.password2);

        user2 = (MaterialEditText) v.findViewById(R.id.username2);

        checkBox = (CheckBox) v.findViewById(R.id.check_admin);
        checkBox1 = (CheckBox) v.findViewById(R.id.check_admin2);

        mCardView = (CardView) v.findViewById(R.id.cardview1);
        mImgLogo = (CircleImageView) v.findViewById(R.id.img_logo_company);
        mTxtNameCompany = (TextView) v.findViewById(R.id.txt_name_company);
        mTxtAddress = (TextView) v.findViewById(R.id.txt_address_company);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Models.FILE_URI, mFileUri);
    }

    public void init() {

        Company company = SaveData.company;

        if (company.name != null) {
            nameCompany.setText(company.name);
            mTxtNameCompany.setText(company.getName());
        }

        if (company.address != null) {
            addressCompany.setText(company.address);
            mTxtAddress.setText(company.getAddress());
        }

        String logo = company.getLogo();

        Glide.with(getActivity()).load(convertToBytes(logo))
                .asBitmap()
                .placeholder(R.mipmap.ic_launcher)
                .into(mImgLogo);

        if (company.user1 != null) {
            user1.setText(company.user1);
        }

        if (company.user2 != null) {
            user2.setText(company.user2);
        }

        if (company.pass1 != null) {
            pass1.setText(company.pass1);
        }

        if (company.pass2 != null) {
            pass2.setText(company.pass2);
        }

        if (company.user1_admin.equals("1")) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        if (company.user2_admin.equals("1")) {
            checkBox1.setChecked(true);
        } else {
            checkBox1.setChecked(false);
        }

    }

    private void addEvents() {
        mCardView.setOnClickListener(new Events());
        mImgLogo.setOnClickListener(new Events());
        nameCompany.addTextChangedListener(new Events());
        addressCompany.addTextChangedListener(new Events());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {

//            mDrawable = mImgLogo.getDrawable().getConstantState().newDrawable();
            if (requestCode == SELECT_PICTURE) {
                try {
                    mImgLogo.setImageBitmap(new UserPicture(data.getData(), getActivity().getContentResolver()).getBitmap());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

                Log.d("SettingFragment", mFileUri.getPath());
                previewCapturedImage(mFileUri.getPath());
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            getShowMessage("User cancelled image capture");
        }

    }

    private boolean isDriverSupportCamera() {
        return getActivity().getApplicationContext().getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int SELECT_PICTURE = 1;

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() +
                File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    private void previewCapturedImage(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            mImgLogo.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    private class Events implements View.OnClickListener, TextWatcher {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cardview1:
                    onClickSaveCompany();
                    break;
                case R.id.img_logo_company:
                    onClickChangeLogo();
                    break;
                case R.id.txt_camera:
                    onClickOpenCamera();
                    break;
                case R.id.txt_gallery:
                    onClickOpenGallery();
                    break;
            }
        }

        private void onClickOpenCamera() {
            if (isDriverSupportCamera()) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                mDialog.dismiss();
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
                mDialog.dismiss();
            } else {
                getShowMessage("Driver do not Support");
            }
        }


        private void onClickChangeLogo() {

            mDialog = new Dialog(getActivity());

            mDialog.setContentView(R.layout.item_select_logo);
            mDialog.setTitle("Avatar");
            mDialog.show();

            mTxtCamera = (TextView) mDialog.findViewById(R.id.txt_camera);
            mTxtGallery = (TextView) mDialog.findViewById(R.id.txt_gallery);

            mTxtCamera.setOnClickListener(new Events());
            mTxtGallery.setOnClickListener(new Events());
        }

        private void onClickSaveCompany() {

            Company company = SaveData.company;

            company.setName(nameCompany.getText().toString());
            company.setAddress(nameCompany.getText().toString());

            String logo = convertToBitmap(mImgLogo);
            logo = Models.FIRST_BASE64 + logo;

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
            if (nameCompany.isFocused()) {
                mTxtNameCompany.setText(editable.toString());
            } else {
                mTxtAddress.setText(editable.toString());
            }
        }
    }

    private void getShowMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private byte[] convertToBytes(String path) {
        path = path.substring(path.indexOf(",") + 1);
        Log.d(TAG, path);
        return Base64.decode(path, Base64.NO_WRAP);
    }

    private String convertToBitmap(ImageView imageView) {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
}
