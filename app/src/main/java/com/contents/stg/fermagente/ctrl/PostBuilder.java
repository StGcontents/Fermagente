package com.contents.stg.fermagente.ctrl;

import android.location.Address;

import com.contents.stg.fermagente.model.Post;

import java.io.File;
import java.util.Date;

public class PostBuilder extends Builder<Post> {

    public PostBuilder() {
        object = new Post();
    }

    public void buildComment(String comment) {
        object.setComment(comment);
    }

    public void buildPosition(Address address) {
        object.setPlace(address.getAddressLine(0));
    }

    public void buildDate(Date date) {
        object.setDate(date);
    }

    public void buildRating(int rating) {
        object.setStars(rating);
    }

    public void buildPhoto(File photo) { object.setPhoto(photo); }

    @Override
    public Post retrieveObject() {
        return object;
    }

    @Override
    public boolean isReady() {
        return object.getPlace() != null && object.getPhoto() != null;
    }
}
