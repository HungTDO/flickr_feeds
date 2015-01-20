package com.framgia.flickrfeeds.core;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quannh on 1/12/15.
 */
public class Album extends BaseImage {
    private List<BaseImage> imageList;

    /**
     * Must implement CREATOR
     */
    public static final Parcelable.Creator<Album> CREATOR
            = new Parcelable.Creator<Album>() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    private Album(Parcel in) {
        super(in);
        imageList = new ArrayList<>();
        in.readTypedList(imageList, BaseImage.CREATOR);
    }

    public Album(List<BaseImage> imageList) {
        this.imageList = imageList;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(imageList);
    }

    public List<BaseImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<BaseImage> imageList) {
        this.imageList = imageList;
    }
}
