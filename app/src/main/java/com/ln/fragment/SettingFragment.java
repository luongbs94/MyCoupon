package com.ln.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.ln.api.LoveCouponAPI;
import com.ln.api.SaveData;
import com.ln.model.Company;
import com.ln.model.Models;
import com.ln.model.UserPicture;
import com.ln.mycoupon.MainApplication;
import com.ln.mycoupon.R;
import com.ln.views.CircleImageView;
import com.rengwuxian.materialedittext.MaterialEditText;

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


    private Firebase mRoot =
            new Firebase("https://nhahv-firebase.firebaseio.com/");

    private static final String TAG = "SettingFragment";
    private MaterialEditText nameCompany, addressCompany, user1, pass1, user2, pass2;
    private CheckBox checkBox, checkBox1;
    private CardView mCardView;
    private LoveCouponAPI mLoveCouponAPI;
    private CircleImageView mImgLogo;
    private TextView mTxtNameCompany, mTxtAddress;
    private TextView mTxtCamera, mTxtGallery;
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
        View v = inflater.inflate(R.layout.layout_setting, container, false);

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

        init();


        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Models.FILE_URI, mFileUri);
    }

    public void init() {

//        String logo = "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";
        String logo = "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";
        byte[] bytes = Base64.decode(logo, Base64.NO_WRAP);
        Glide.with(getActivity()).load(bytes).asBitmap().into(mImgLogo);
        Company company = SaveData.company;
        if (company.name != null) {
            nameCompany.setText(company.name);
            mTxtNameCompany.setText(company.getName());
        }

        if (company.address != null) {
            addressCompany.setText(company.address);
            mTxtAddress.setText(company.getAddress());
        }

//        if (company.getLogo() != null) {
////            mImgLogo.setImageBitmap(convertToBitmap(company.getLogo()));
////            Picasso.with(getActivity()).loa
//            Glide.with(getActivity()).load(convertToBitmap(company.getLogo())).asBitmap().into(mImgLogo);
//        }

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


        addEvents();
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

    private byte[] convertToBitmap(String path) {
        path = path.substring(path.indexOf(",") + 1);
        Log.d(TAG, path.substring(path.indexOf(",") + 1));
        return Base64.decode(path, Base64.NO_WRAP);
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
