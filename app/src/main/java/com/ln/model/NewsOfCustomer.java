package com.ln.model;

import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Nhahv on 7/3/2016.
 * <></>
 */

@Table(name = "NewsOfCustomer")
public class NewsOfCustomer extends Model implements Comparable<NewsOfCustomer> {

    @Column(name = "message_id")
    private String message_id;

    @Column(name = "content")
    private String content;

    @Column(name = "created_date")
    private long created_date;

    @Column(name = "company_id")
    private String company_id;

    @Column(name = "last_date")
    private long last_date;

    @Column(name = "title")
    private String title;

    @Column(name = "link")
    private String link;

    @Column(name = "images_link")
    private String images_link;

    @Column(name = "logo")
    private String logo;

    @Column(name = "logo_link")
    private String logo_link;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private int type;

    @Column(name = "isLike")
    private boolean isLike = false;

    @Column(name = "isDelete")
    private boolean isDelete = false;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated_date() {
        return created_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public long getLast_date() {
        return last_date;
    }

    public void setLast_date(long last_date) {
        this.last_date = last_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImages_link() {
        return images_link;
    }

    public void setImages_link(String images_link) {
        this.images_link = images_link;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo_link() {
        return logo_link;
    }

    public void setLogo_link(String logo_link) {
        this.logo_link = logo_link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @Override
    public int compareTo(@NonNull NewsOfCustomer another) {
        if (created_date > another.getCreated_date()) {
            return -1;
        }
        return 1;
    }
}
