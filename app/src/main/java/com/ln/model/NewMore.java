package com.ln.model;

import android.support.annotation.NonNull;

import com.ln.databases.DatabaseManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import static android.R.attr.type;

/**
 * Created by Nhahv on 9/14/2016.
 * <></>
 */
@Table(database = DatabaseManager.class)
public class NewMore extends BaseModel implements Comparable<NewsOfCustomer> {

    @PrimaryKey
    @Index
    @Column
    private String message_id;

    @Column
    private String content;

    @Column
    private long created_date;

    @Column
    private String company_id;

    @Column
    private long last_date;

    @Column
    private String title;

    @Column
    private String link;

    @Column
    private String images_link;

    @Column
    private String logo_link;

    @Column
    private String name;

    @Column
    private String user;

    @Column
    private boolean isLike = false;

    @Column
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int compareTo(@NonNull NewsOfCustomer another) {
        if (created_date > another.getCreated_date()) {
            return -1;
        }
        return 1;
    }
}
