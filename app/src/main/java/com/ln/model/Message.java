package com.ln.model;

public class Message {

    private String message_id;
    private String content;
    private String company_id;
    private String title;
    private String link;
    private String images_link;
    private String logo;
    private String logo_link;
    private String name;

    private boolean isLike;
    private boolean isDelete;

//    private Date last_date, created_date;


    public Message() {
    }

    public String getLink() {
        return link;
    }


    public String getMessage_id() {
        return message_id;
    }


    public String getContent() {
        return content;
    }


    public String getCompany_id() {
        return company_id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages_link() {
        return images_link;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setLink(String link) {
        this.link = link;
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

    public Message(NewsOfCustomer news) {
        this.message_id = news.getMessage_id();
        this.content = news.getContent();
        this.company_id = news.getCompany_id();
        this.title = news.getTitle();
        this.link = news.getLink();
        this.images_link = news.getImages_link();
        this.logo = news.getLogo();
        this.logo_link = news.getLogo_link();
        this.name = news.getName();
    }

    public Message(NewsOfMore news) {
        this.message_id = news.getMessage_id();
        this.content = news.getContent();
        this.company_id = news.getCompany_id();
        this.title = news.getTitle();
        this.link = news.getLink();
        this.images_link = news.getImages_link();
        this.logo_link = news.getLogo_link();
        this.name = news.getName();
    }
    public Message(NewsOfMore news, boolean isLike) {
        this.message_id = news.getMessage_id();
        this.content = news.getContent();
        this.company_id = news.getCompany_id();
        this.title = news.getTitle();
        this.link = news.getLink();
        this.images_link = news.getImages_link();
        this.logo_link = news.getLogo_link();
        this.name = news.getName();
        this.isLike = isLike;
    }
    public Message(NewsOfCustomer news, boolean isLike) {
        this.message_id = news.getMessage_id();
        this.content = news.getContent();
        this.company_id = news.getCompany_id();
        this.title = news.getTitle();
        this.link = news.getLink();
        this.images_link = news.getImages_link();
        this.logo = news.getLogo();
        this.logo_link = news.getLogo_link();
        this.name = news.getName();
        this.isLike = isLike;
    }
}

