package com.framgia.flickrfeeds.core;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import com.framgia.flickrfeeds.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quannh on 1/12/15.
 */
public class AlbumManager {
    public static final String TAG = AlbumManager.class.getSimpleName();

    private static int imageThumbSize;

    /**
     * Get list of album
     * @param context
     * @return
     */
    public static List<Album> getAlbumList(Context context) {
        imageThumbSize = context.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        List<Album> albums = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();

        //
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String[] projection = {Media._ID,
                Media.BUCKET_ID,
                Media.BUCKET_DISPLAY_NAME,
                Media.DATE_TAKEN,
                Media.DATA};

        Cursor cursor = cr.query(uri, projection, null, null, null);

        ArrayList<String> ids = new ArrayList<String>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Album album = new Album();

                int columnIndex = cursor.getColumnIndex(Media.BUCKET_ID);
                album.setId(cursor.getString(columnIndex));

                // Check album list had this album
                if (!ids.contains(album.getId())) {
                    // Add album detail
                    columnIndex = cursor.getColumnIndex(Media.BUCKET_DISPLAY_NAME);
                    album.setName(cursor.getString(columnIndex));

                    columnIndex = cursor.getColumnIndex(Media.DATA);
                    String thumbnailPath = cursor.getString(columnIndex);

                    album.setThumb(getAlbumCover(thumbnailPath));
                    albums.add(album);
                    ids.add(album.getId());
                } else {
                    // TODO: count number of albums
                }
            }
            cursor.close();
        }
        return albums;
    }

    /**
     * @param thumbnailPath
     * @return
     */
    private static Bitmap getAlbumCover(String thumbnailPath) {
        File imgFile = new File(thumbnailPath);
        Bitmap imageBitmap = null;

        if (imgFile.exists()) {
            imageBitmap = decodeSampledBitmapFromFile(imgFile.getAbsolutePath(), imageThumbSize, imageThumbSize);
        }
        return imageBitmap;
    }

    /**
     * Decode bitmap image from file.
     *
     * @param filename
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    /**
     * Calculate image in sample size.
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            /*
            Calculate the largest inSampleSize value that is a power of 2 and keeps both
            height and width larger than the requested height and width.
            */
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
