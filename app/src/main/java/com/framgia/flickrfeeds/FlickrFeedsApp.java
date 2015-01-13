package com.framgia.flickrfeeds;

import android.app.Application;
import android.content.Context;

import com.framgia.flickrfeeds.core.ImageCache;
import com.framgia.flickrfeeds.core.ImageLoader;

/**
 * Created by quannh on 1/13/15.
 */
public class FlickrFeedsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(0.25f);
        ImageLoader.getInstance().init(context, cacheParams);
    }
}
