package com.ln.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Nhahv on 6/30/2016.
 * <></>
 */
@Table(name = "NewsOfCompany")
public class NewsOfCompany extends Model {

    @Column(name = "message_id", index = true)
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

    @Column(name = "like")
    private boolean like;


    public NewsOfCompany() {
    }

    public NewsOfCompany(String message_id) {
        this.message_id = message_id;
    }

    public NewsOfCompany(String message_id, String content,
                         String company_id, long last_date, String title,
                         String link, String images_link) {
        this.message_id = message_id;
        this.content = content;
        this.company_id = company_id;
        this.last_date = last_date;
        this.title = title;
        this.link = link;
        this.images_link = images_link;
    }

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


    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
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

    public void setLast_date(long last_date) {
        this.last_date = last_date;
    }

    public void setCreated_date(long created_date) {
        this.created_date = created_date;
    }

    public long getCreated_date() {
        return created_date;
    }

    public long getLast_date() {
        return last_date;
    }


    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }
}
