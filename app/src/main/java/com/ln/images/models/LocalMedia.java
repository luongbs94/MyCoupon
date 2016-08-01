package com.ln.images.models;


import org.parceler.Parcel;

/**
 * Created by dee on 2015/8/5.
 * <></>
 */
@Parcel
public class LocalMedia {

    public String path;

    public LocalMedia() {
    }

    public LocalMedia(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
