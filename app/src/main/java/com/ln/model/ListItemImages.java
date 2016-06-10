package com.ln.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nhahv on 6/8/2016.
 */
public class ListItemImages implements Serializable {
    private List<ItemImage> mListImages = new ArrayList<>();

    public ListItemImages() {
    }

    public List<ItemImage> getListImages() {
        return mListImages;
    }

    public void setListImages(List<ItemImage> mListImages) {
        this.mListImages = mListImages;
    }
}
