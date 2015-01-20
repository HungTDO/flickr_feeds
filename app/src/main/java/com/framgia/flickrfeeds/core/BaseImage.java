package com.framgia.flickrfeeds.core;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by quannh on 1/13/15.
 */
public class BaseImage implements Parcelable {
    protected String id;
    protected String bucketId;
    protected String bucketName;
    protected String dateTaken;
    protected String size;
    protected String mimeType;
    protected Uri dataUri;

    public static final Parcelable.Creator<BaseImage> CREATOR
            = new Parcelable.Creator<BaseImage>() {
        public BaseImage createFromParcel(Parcel in) {
            return new BaseImage(in);
        }

        public BaseImage[] newArray(int size) {
            return new BaseImage[size];
        }
    };

    public BaseImage() {
        id = new String();
        bucketId = new String();
        bucketName = new String();

        dateTaken = new String();
        size = new String();
        mimeType = new String();
        dataUri = Uri.parse(new String());
    }

    protected BaseImage (BaseImage image) {
        this.id = image.getId();
        this.bucketId = image.getBucketId();
        this.bucketName = image.getBucketName();

        this.dateTaken = image.getDateTaken();
        this.size = image.getSize();
        this.mimeType = image.getMimeType();
        this.dataUri = image.getDataUri();
    }

    protected BaseImage(Parcel in) {
        id = in.readString();
        bucketId = in.readString();
        bucketName = in.readString();

        dateTaken = in.readString();
        size = in.readString();
        mimeType = in.readString();
        dataUri = Uri.parse(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.bucketId);
        dest.writeString(this.bucketName);

        dest.writeString(this.dateTaken);
        dest.writeString(this.size);
        dest.writeString(this.mimeType);
        dest.writeString(this.dataUri.toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Uri getDataUri() {
        return dataUri;
    }

    public void setDataUri(Uri dataUri) {
        this.dataUri = dataUri;
    }
}
