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
    public static int TYPE_IMAGE_MANAGER = TYPE_ALL_IMAGE;

    public static List<LocalMedia> loadImages(Context context, int type) {

        TYPE_IMAGE_MANAGER = type;
        List<LocalMedia> listLocalMedia = new ArrayList<>();
        switch (TYPE_IMAGE_MANAGER) {
            case TYPE_ALL_IMAGE:
                listLocalMedia.addAll
                        (getImages(context,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI));
                listLocalMedia.addAll
                        (getImages(context,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                break;
            case TYPE_INTERNAL:
                listLocalMedia.addAll
                        (getImages(context,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI));
                break;
            case TYPE_SD_CARD:
                listLocalMedia.addAll
                        (getImages(context,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                break;
            default:
                listLocalMedia.addAll
                        (getImages(context,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI));
                listLocalMedia.addAll
                        (getImages(context,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                break;
        }

        return listLocalMedia;
    }

    private static List<LocalMedia> getImages(Context context, Uri uris) {

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
}
