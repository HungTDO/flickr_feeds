package com.framgia.flickrfeeds.core;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by quannh on 1/12/15.
 */
public class AlbumManager {
    public static final String TAG = AlbumManager.class.getSimpleName();

    public static String[] PROJECTION = new String[]{
            Media._ID,
            Media.BUCKET_ID,
            Media.BUCKET_DISPLAY_NAME,
            Media.DATE_TAKEN,
            Media.SIZE,
            Media.MIME_TYPE,
            Media.DATA
    };

    // Group by bucket_id.
    public static String GALLERY_SELECTION = "1) GROUP BY 2,(2";
    // Select all image in album
    public static String ALBUM_SELECTION = Media.BUCKET_ID + "=?";
    // Select all image detail
    public static String IMAGE_SELECTION = Media._ID + "=?";

    // Order by max date taken
    public static String DATETAKEN_ORDER_BY_DESC = Media.DATE_TAKEN + " DESC";
    public static String DATETAKEN_ORDER_BY_ASC = Media.DATE_TAKEN + " ASC";
    public static String BUCKET_NAME_ORDER_BY = Media.BUCKET_DISPLAY_NAME + " ASC";
    public static String DATA_SIZE_ORDER_BY = Media.SIZE + " ASC";

    /**
     * Get list of album
     *
     * @param cr content resolver
     * @return list of album.
     */
    public static List<BaseImage> getAlbumList(ContentResolver cr, String[] projection,
                                               String selection, String[] selectionArgs, String sortOrder) {
        List<BaseImage> list = new ArrayList<BaseImage>();

        Uri uri = Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                BaseImage image = generateBaseImageFromCursor(cursor);
                list.add(image);
            }
            cursor.close();
        }
        return list;
    }


    /**
     * Generate BaseImage from cursor.
     *
     * @param cursor
     * @return
     */
    private static BaseImage generateBaseImageFromCursor(Cursor cursor) {
        int idColumn = cursor.getColumnIndex(Media._ID);
        int bucketIdColumn = cursor.getColumnIndex(Media.BUCKET_ID);
        int bucketNameColumn = cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME);

        int dateColumn = cursor.getColumnIndex(Media.DATE_TAKEN);

        int sizeColumn = cursor.getColumnIndex(Media.SIZE);
        int mimeColumn = cursor.getColumnIndex(Media.MIME_TYPE);
        int dataColumn = cursor.getColumnIndex(Media.DATA);

        BaseImage image = new BaseImage();

        // set image detail
        image.setId(cursor.getString(idColumn));
        image.setBucketId(cursor.getString(bucketIdColumn));
        image.setBucketName(cursor.getString(bucketNameColumn));

        image.setDateTaken(cursor.getString(dateColumn));
        image.setMimeType(cursor.getString(mimeColumn));
        image.setId(cursor.getString(idColumn));
        image.setSize(cursor.getString(sizeColumn));

        // set first image as album cover
        String thumbnailPath = cursor.getString(dataColumn);
        image.setDataUri(Uri.parse(thumbnailPath));

        return image;
    }
}
