package com.ln.model;

/**
 * Created by Nhahv on 6/21/2016.
 * <></>
 */
public class NewsOfLike extends Message {

    private boolean isLike;
    private boolean isDelete;


    public NewsOfLike(Message message, boolean isLike) {
        setMessage_id(message.getMessage_id());
        setContent(message.getContent());
        setLike(isLike);
        setCompany_id(message.getCompany_id());
        setCreated_date(message.getCreated_date());
        setLink(message.getLink());
        setImages_link(message.getImages_link());
        setName(message.getName());
        setLogo(message.getLogo());
        setTitle(message.getTitle());
        setLast_date(message.getLast_date());
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
}
