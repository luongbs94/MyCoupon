package com.ln.until;

/**
 * Created by Nhahv on 6/30/2016.
 * <></>
 */
public class UntilNews {

    private String message_id;
    private String content;
    private long created_date;
    private String company_id;
    private long last_date;
    private String title;
    private String link;
    private String images_link;


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
}