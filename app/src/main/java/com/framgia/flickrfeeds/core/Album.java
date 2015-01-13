package com.framgia.flickrfeeds.core;

import java.util.List;

/**
 * Created by quannh on 1/12/15.
 */
public class Album extends BaseImage {
    private List<BaseImage> imageList;

    public Album() {
        //
    }

    public Album(List<BaseImage> imageList) {
        this.imageList = imageList;
    }

    public List<BaseImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<BaseImage> imageList) {
        this.imageList = imageList;
    }
}
