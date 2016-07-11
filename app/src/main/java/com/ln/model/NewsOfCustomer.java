package com.ln.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nhahv on 7/3/2016.
 * <></>
 */
public class NewsOfCustomer extends RealmObject {

    @PrimaryKey
    private String message_id;
    private String content;
    private long created_date;
    private String company_id;
    private long last_date;
    private String title;
    private String link;
    private String images_link;
    private String logo;
    private String logo_link;
    private String name;

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


    public void setNewsOfCustomer(String message_id, String content, String company_id,
                                  String title, String link, String images_link,
                                  String logo, String logo_link, String name) {
        this.message_id = message_id;
        this.content = content;
        this.company_id = company_id;
        this.title = title;
        this.link = link;
        this.images_link = images_link;
        this.logo = logo;
        this.logo_link = logo_link;
        this.name = name;
    }
}