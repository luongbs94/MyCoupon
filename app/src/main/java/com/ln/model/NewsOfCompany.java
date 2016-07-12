package com.ln.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nhahv on 6/30/2016.
 * <></>
 */
public class NewsOfCompany extends RealmObject {

    @PrimaryKey
    private String message_id;
    private String content;
    private long created_date;
    private String company_id;
    private long last_date;
    private String title;
    private String link;
    private String images_link;

    public NewsOfCompany() {
    }

    public NewsOfCompany(String message_id) {
        this.message_id = message_id;
    }

    public NewsOfCompany(String message_id, String content, long created_date,
                         String company_id, long last_date, String title,
                         String link, String images_link) {
        this.message_id = message_id;
        this.content = content;
        this.created_date = created_date;
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

}
