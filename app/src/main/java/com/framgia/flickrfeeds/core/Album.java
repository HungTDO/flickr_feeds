package com.framgia.flickrfeeds.core;

import android.graphics.Bitmap;

/**
 * Created by quannh on 1/12/15.
 */
public class Album {
    private String name;
    private String id;
    private Bitmap thumb;
    private IImageList imageList;

    public Album() {
        //
    }

    public Album(String name, String id, IImageList imageList) {
        this.name = name;
        this.id = id;
        this.imageList = imageList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public IImageList getImageList() {
        return imageList;
    }

    public void setImageList(IImageList imageList) {
        this.imageList = imageList;
    }
}
