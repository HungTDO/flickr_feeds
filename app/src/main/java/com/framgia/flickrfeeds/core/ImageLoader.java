package com.framgia.flickrfeeds.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ImageView;

import com.framgia.flickrfeeds.core.ImageCache.ImageCacheParams;
import com.framgia.flickrfeeds.util.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by quannh on 1/13/15.
 */
public class ImageLoader {
    private Resources resource;

    private ImageCache imageCache;
    private ImageCache bitmapOptionCache;
    private static final int FADE_IN_TIME = 500;

    private int imageWidth = 0;
    private int imageHeight = 0;

    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

    private static ImageLoader instance = null;

    /**
     * Initial ImageLoader option.
     * This should be call from Application class.
     * @param context
     * @param cacheParams
     * @return
     */
    public synchronized void init(Context context, ImageCacheParams cacheParams) {
        if (imageCache == null) {
            imageCache = ImageCache.getInstance(cacheParams);
        }

        ImageCache.ImageCacheParams bitmapOptionCacheParams =
                new ImageCache.ImageCacheParams(0.25f);
        bitmapOptionCache = ImageCache.getInstance(cacheParams);

        resource = context.getResources();
    }

    /**
     * Get ImageLoader singleton instance.
     *
     * @return singleton instance.
     */
    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    /**
     *
     */
    protected ImageLoader() {
    }

    /**
     * Set image size by longest size.
     *
     * @param longestSize
     * @return
     */
    public ImageLoader setImageSize(int longestSize) {
        return setImageSize(longestSize, longestSize);
    }

    /**
     * Set image size.
     *
     * @param imageWidth
     * @param imageHeight
     */
    public ImageLoader setImageSize(int imageWidth, int imageHeight) {
        setImageWidth(imageWidth);
        setImageHeight(imageHeight);
        return instance;
    }

    /**
     * /**
     *
     * @param uri
     * @param imageView
     */
    public void displayImage(Uri uri, ImageView imageView) {
        if (uri == null) {
            return;
        }

        BitmapDrawable value = null;

        if (imageCache != null) {
            // Caching BitmapDrawable with exactly size.
            value = imageCache.getBitmapFromMemCache(String.valueOf(uri) + imageWidth + imageHeight);
        }

        if (value != null) {
            // Bitmap found in memory cache
            imageView.setImageDrawable(value);
        } else if (cancelPotentialWork(uri, imageView)) {
            final BitmapProcessor task = new BitmapProcessor(uri, imageView);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(resource, null, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute();
        }
    }


    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
        setPauseWork(false);
    }

    /**
     * Returns true if the current work has been canceled or if there was no work in
     * progress on this image view.
     * Returns false if the work in progress deals with the same data. The work is not
     * stopped in that case.
     */
    public static boolean cancelPotentialWork(Uri uri, ImageView imageView) {
        //BEGIN_INCLUDE(cancel_potential_work)
        final BitmapProcessor bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Uri bitmapUri = bitmapWorkerTask.uri;
            if (bitmapUri == null || !bitmapUri.equals(uri)) {
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
        //END_INCLUDE(cancel_potential_work)
    }

    /**
     * Pause any ongoing background work. This can be used as a temporary
     * measure to improve performance. For example background work could
     * be paused when a ListView or GridView is being scrolled using a
     * {@link android.widget.AbsListView.OnScrollListener} to keep
     * scrolling smooth.
     * <p/>
     * If work is paused, be sure setPauseWork(false) is called again
     * before your fragment or activity is destroyed (for example during
     * {@link android.app.Activity#onPause()}), or there is a risk the
     * background thread will never finish.
     */
    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private class BitmapProcessor extends AsyncTask<Void, Void, BitmapDrawable> {
        private Uri uri;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapProcessor(Uri uri, ImageView imageView) {
            this.uri = uri;
            this.imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Background processing.
         */
        @Override
        protected BitmapDrawable doInBackground(Void... params) {
            final String dataString = String.valueOf(uri);
            Bitmap bitmap = null;
            BitmapDrawable drawable = null;


            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            bitmap = decodeSampledBitmapFromFile(dataString, imageWidth, imageHeight);

            // If the bitmap was processed and the image cache is available, then add the processed
            // bitmap to the cache for future use. Note we don't check if the task was cancelled
            // here, if it was, and the thread is still running, we may as well add the processed
            // bitmap to our cache as it might be used again in the future
            if (bitmap != null) {
                if (Utils.hasHoneycomb()) {
                    // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
                    drawable = new BitmapDrawable(resource, bitmap);
                } else {
                    // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
                    // which will recycle automagically
                    drawable = new RecyclingBitmapDrawable(resource, bitmap);
                }

                if (imageCache != null) {
                    // Add image size to cache, caching different bitmap size
                    imageCache.addBitmapToCache(dataString + imageWidth + imageHeight, drawable);
                }
            }
            return drawable;
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            // if cancel was called on this task or the "exit early" flag is set then we're done
            if (isCancelled() || mExitTasksEarly) {
                drawable = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (drawable != null && imageView != null) {
                setImageDrawable(imageView, drawable);
            }
        }

        /**
         * Returns the ImageView associated with this task as long as the ImageView's task still
         * points to this task as well. Rerwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapProcessor bitmapProcessor = getBitmapWorkerTask(imageView);

            if (this == bitmapProcessor) {
                return imageView;
            }

            return null;
        }
    }

    /**
     * Get BitmapWorkerTask working with imageView.
     *
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private static BitmapProcessor getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapProcessorTask();
            }
        }
        return null;
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work is in progress.
     * Contains a reference to the actual worker task, so that it can be stopped if a new binding is
     * required, and makes sure that only the last started worker process can bind its result,
     * independently of the finish order.
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapProcessor> bitmapProcessorReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapProcessor bitmapProcessor) {
            super(res, bitmap);
            bitmapProcessorReference =
                    new WeakReference<BitmapProcessor>(bitmapProcessor);
        }

        public BitmapProcessor getBitmapProcessorTask() {
            return bitmapProcessorReference.get();
        }
    }

    /**
     * Decode bitmap image from file.
     *
     * @param filename
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // If we're running on Honeycomb or newer, try to use inBitmap
        if (Utils.hasHoneycomb()) {
            addInBitmapOptions(options, bitmapOptionCache);
        }

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) {
        //BEGIN_INCLUDE(add_bitmap_options)
        // inBitmap only works with mutable bitmaps so force the decoder to
        // return mutable bitmaps.
        options.inMutable = true;

        if (cache != null) {
            // Try and find a bitmap to use for inBitmap
            Bitmap inBitmap = cache.getBitmapFromReusableSet(options);

            if (inBitmap != null) {
                options.inBitmap = inBitmap;
            }
        }
        //END_INCLUDE(add_bitmap_options)
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

            // Anything more than 2x the requested pixels, sample down further
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        // Transition drawable with a transparent drawable and the final drawable
        final TransitionDrawable td =
                new TransitionDrawable(new Drawable[]{
                        new ColorDrawable(android.R.color.transparent),
                        drawable
                });

        imageView.setImageDrawable(td);
        td.startTransition(FADE_IN_TIME);
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}
