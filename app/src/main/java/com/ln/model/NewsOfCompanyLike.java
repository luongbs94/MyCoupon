package com.ln.model;

/**
 * Created by Nhahv on 7/4/2016.
 * <></>
 */
public class NewsOfCompanyLike {

    private String message_id;
    private String content;
    //    private int created_date;
    private String company_id;
    //    private Date last_date;
    private String title;
    private String link;
    private String images_link;

    private boolean isLike;

    public NewsOfCompanyLike() {
    }

    public NewsOfCompanyLike(NewsOfCompany news) {
        this.message_id = news.getMessage_id();
        this.content = news.getContent();
        this.company_id = news.getCompany_id();
        this.title = news.getTitle();
        this.link = news.getLink();
        this.images_link = news.getImages_link();
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

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}