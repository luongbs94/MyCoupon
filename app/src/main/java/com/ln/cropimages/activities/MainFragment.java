package com.ln.cropimages.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.isseiaoki.simplecropview.CropImageView;
import com.ln.mycoupon.R;


public class MainFragment extends Fragment {
    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    private static final String PROGRESS_DIALOG = "ProgressDialog";

    private CropImageView mCropView;
    private LinearLayout mRootLayout;

    public MainFragment() {
    }

    public static MainFragment getInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null, false);
    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        bindViews(view);
//        // apply custom font
////        FontUtils.setFont(mRootLayout);
////        mCropView.setDebug(true);
//        // set bitmap to CropImageView
//        if (mCropView.getImageBitmap() == null) {
//            mCropView.setImageResource(R.drawable.sample5);
//        }
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent result) {
//        super.onActivityResult(requestCode, resultCode, result);
//        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
//            showProgress();
//            mCropView.startLoad(result.getData(), mLoadCallback);
//        } else if (requestCode == REQUEST_SAF_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
//            showProgress();
//            mCropView.startLoad(Utils.ensureUriPermission(getContext(), result), mLoadCallback);
//        }
//    }
//
//
//    private void bindViews(View view) {
//        mCropView = (CropImageView) view.findViewById(R.id.cropImageView);
//        view.findViewById(R.id.buttonDone).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
//        view.findViewById(R.id.button1_1).setOnClickListener(btnListener);
//        view.findViewById(R.id.button3_4).setOnClickListener(btnListener);
//        view.findViewById(R.id.button4_3).setOnClickListener(btnListener);
//        view.findViewById(R.id.button9_16).setOnClickListener(btnListener);
//        view.findViewById(R.id.button16_9).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonFree).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonPickImage).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
//        view.findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(btnListener);
//        mRootLayout = (LinearLayout) view.findViewById(R.id.layout_root);
//    }
//
//    //    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//    public void pickImage() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), REQUEST_PICK_IMAGE);
//        } else {
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//            startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
//        }
//    }
//
//    //    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    public void cropImage() {
//        showProgress();
//        mCropView.startCrop(createSaveUri(), mCropCallback, mSaveCallback);
//    }
//
//
//    public void showProgress() {
//        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
//        getFragmentManager()
//                .beginTransaction()
//                .add(f, PROGRESS_DIALOG)
//                .commitAllowingStateLoss();
//    }
//
//    public void dismissProgress() {
//        if (!isAdded()) return;
//        android.support.v4.app.FragmentManager manager = getFragmentManager();
//        if (manager == null) return;
//        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
//        if (f != null) {
//            getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
//        }
//    }
//
//    public Uri createSaveUri() {
//        return Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
//    }
//
//    private final View.OnClickListener btnListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.buttonDone:
//                    MainFragmentPermissionsDispatcher.cropImageWithCheck(MainFragment.this);
//                    break;
//                case R.id.buttonFitImage:
//                    mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
//                    break;
//                case R.id.button1_1:
//                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);
//                    break;
//                case R.id.button3_4:
//                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
//                    break;
//                case R.id.button4_3:
//                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
//                    break;
//                case R.id.button9_16:
//                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
//                    break;
//                case R.id.button16_9:
//                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
//                    break;
//                case R.id.buttonCustom:
//                    mCropView.setCustomRatio(7, 5);
//                    break;
//                case R.id.buttonFree:
//                    mCropView.setCropMode(CropImageView.CropMode.FREE);
//                    break;
//                case R.id.buttonCircle:
//                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
//                    break;
//                case R.id.buttonShowCircleButCropAsSquare:
//                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
//                    break;
//                case R.id.buttonRotateLeft:
//                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
//                    break;
//                case R.id.buttonRotateRight:
//                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
//                    break;
//                case R.id.buttonPickImage:
//                    MainFragmentPermissionsDispatcher.pickImageWithCheck(MainFragment.this);
//                    break;
//            }
//        }
//    };
//
//
//    private final LoadCallback mLoadCallback = new LoadCallback() {
//        @Override
//        public void onSuccess() {
//            dismissProgress();
//        }
//
//        @Override
//        public void onError() {
//            dismissProgress();
//        }
//    };
//
//    private final CropCallback mCropCallback = new CropCallback() {
//        @Override
//        public void onSuccess(Bitmap cropped) {
//        }
//
//        @Override
//        public void onError() {
//        }
//    };
//
//    private final SaveCallback mSaveCallback = new SaveCallback() {
//        @Override
//        public void onSuccess(Uri outputUri) {
//            dismissProgress();
//            ((MainActivity) getActivity()).startResultActivity(outputUri);
//        }
//
//        @Override
//        public void onError() {
//            dismissProgress();
//        }
//    };
}