package com.ln.loadimage;

/**
 * Created by Nhahv on 5/21/2016.
 */

public class LoadImages {
    public ViewHolder viewHolder;
    public String url;

    public LoadImages() {
    }

    public LoadImages(ViewHolder viewHolder, String url) {
        this.viewHolder = viewHolder;
        this.url = url;
    }

    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
