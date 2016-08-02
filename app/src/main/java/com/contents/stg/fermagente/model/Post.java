package com.contents.stg.fermagente.model;

import java.util.Date;

public class Post {

    private String comment, place;
    private Date date;
    private int stars;

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }
}
