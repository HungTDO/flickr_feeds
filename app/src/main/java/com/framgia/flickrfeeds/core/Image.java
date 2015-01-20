package com.framgia.flickrfeeds.core;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by quannh on 1/12/15.
 */
public class Image extends BaseImage {
    protected long width;
    protected long height;

    /**
     * Must implement CREATOR
     */
    public static final Parcelable.Creator<Image> CREATOR
            = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public Image() {
        super();
        this.width = 0;
        this.height = 0;
    }

    public Image (BaseImage image) {
        super(image);
    }

    private Image(Parcel in) {
        super(in);
        width = in.readInt();
        height = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.width);
        dest.writeLong(this.height);
    }


    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

}
