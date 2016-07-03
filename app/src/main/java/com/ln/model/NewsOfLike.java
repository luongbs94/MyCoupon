package com.ln.model;

import java.util.Date;

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
    private Date created_date;
    private String company_id;
    private Date last_date;
    private String title;
    private String link;
    private String images_link;
    private String logo;
    private String logo_link;
    private String name;
    private boolean isLike;
    private boolean isDelete;

    public NewsOfLike() {
    }

    public NewsOfLike(Message message, boolean isLike) {
        setMessage_id(message.getMessage_id());
        setContent(message.getContent());
        setLike(isLike);
        setCompany_id(message.getCompany_id());
        setLink(message.getLink());
        setImages_link(message.getImages_link());
        setName(message.getName());
        setLogo(message.getLogo());
        setTitle(message.getTitle());
    }

    public NewsOfLike(NewsOfCompany message, boolean like) {
        message_id = message.getMessage_id();
        content = message.getContent();
        isLike = like;
        company_id = message.getCompany_id();
//        created_date = message.getCreated_date();
        link = message.getLink();
        images_link = message.getImages_link();
        title = message.getTitle();
//        last_date = message.getLast_date();
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

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public Date getLast_date() {
        return last_date;
    }

    public void setLast_date(Date last_date) {
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
}
