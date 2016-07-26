package com.ln.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nhahv on 6/21/2016.
 * <></>
 */
public class NewsOfLike extends RealmObject {

    @PrimaryKey
    private String message_id;
    private String content;
    private String company_id;
    private String title;
    private String link;
    private String images_link;
    private String logo;
    private String logo_link;
    private String name;

    private String userId;

    private int type;

    public NewsOfLike() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNewsOfLike(Message message, String userId, int type) {
        this.message_id = message.getMessage_id();
        this.content = message.getContent();
        this.company_id = message.getCompany_id();
        this.title = message.getTitle();
        this.link = message.getLink();
        this.images_link = message.getImages_link();
        this.logo = message.getLogo();
        this.logo_link = message.getLogo_link();
        this.name = message.getName();
        this.userId = userId;
        this.type = type;
    }

}
