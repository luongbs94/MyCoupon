package com.ln.model;

/**
 * Created by Nhahv on 6/21/2016.
 * <></>
 */
public class NewsOfUser extends Message {

    private boolean isLike;


    public NewsOfUser(Message message, boolean isLike) {
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
}
