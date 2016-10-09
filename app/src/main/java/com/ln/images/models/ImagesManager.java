package com.ln.images.models;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dee on 15/11/19.
 * <></>
 */
public class ImagesManager {

    public static final int TYPE_ALL_IMAGE = 1;
    public static final int TYPE_INTERNAL = 2;
    public static final int TYPE_SD_CARD = 3;
    private static List<LocalMedia> mListImageAll = new ArrayList<>();
    private static List<LocalMedia> mListImageInternal = new ArrayList<>();
    private static List<LocalMedia> mListImageExternal = new ArrayList<>();


    private static ImagesManager mInstances;

    private ImagesManager(Context context) {
        loadImages(context);
    }

    public static void getInstances(Context context) {
        if (mInstances == null) {
            mInstances = new ImagesManager(context);
        }
    }

    private static void loadImages(Context context) {

        List<LocalMedia> mediasInternal = getImages(context, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        List<LocalMedia> mediasExternal = getImages(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (mediasExternal != null) {
            mListImageExternal.addAll(mediasExternal);
            mListImageAll.addAll(mediasExternal);
        }
        if (mediasInternal != null) {
            mListImageInternal.addAll(mediasInternal);
            mListImageAll.addAll(mediasInternal);
        }
    }

    private static List<LocalMedia> getImages(Context context, Uri uris) {

        // check permission

        List<LocalMedia> listLocalMedia = new ArrayList<>();
        Cursor cursor = context
                .getContentResolver()
                .query(uris, new String[]{"_data"}, null, null, null, null);
        if (cursor == null) {
            return listLocalMedia;
        }

        cursor.moveToFirst();

        int indexData = cursor.getColumnIndex("_data");

        while (!cursor.isAfterLast()) {
            listLocalMedia.add(new LocalMedia(cursor.getString(indexData)));
            cursor.moveToNext();
        }
        cursor.close();
        return listLocalMedia;
    }


    public static List<LocalMedia> getListImageAll() {
        return mListImageAll;
    }

    public static List<LocalMedia> getListImageInternal() {
        return mListImageInternal;
    }

    public static List<LocalMedia> getListImageExternal() {
        return mListImageExternal;
    }
}
