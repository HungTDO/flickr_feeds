package com.framgia.flickrfeeds.core;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION_CODES;
import android.os.StatFs;
import android.support.v4.util.LruCache;

import com.framgia.flickrfeeds.util.Utils;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by quannh on 1/15/15.
 */
public class ImageCache {
    private static final String TAG = "ImageCache";

    // Default memory cache size in kilobytes
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 5; // 5MB

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;

    private LruCache<String, BitmapDrawable> memCache;
    private ImageCacheParams cacheParams;

    private Set<SoftReference<Bitmap>> reusableBitmaps;

    /**
     * Create a new ImageCache object using the specified parameters.
     *
     * @param cacheParams The cache parameters to use to initialize the cache
     */
    private ImageCache(ImageCacheParams cacheParams) {
        init(cacheParams);
    }

    /**
     * Return an {@link ImageCache} instance.
     *
     * @param cacheParams     The cache parameters to use if the ImageCache needs instantiation.
     * @return An existing retained ImageCache object or a new one if one did not exist
     */
    public static ImageCache getInstance(ImageCacheParams cacheParams) {

        // See if we already have an ImageCache stored in RetainFragment
        ImageCache imageCache = new ImageCache(cacheParams);

        return imageCache;
    }

    /**
     * Initialize the cache, providing all parameters.
     *
     * @param cacheParams The cache parameters to initialize the cache
     */
    private void init(ImageCacheParams cacheParams) {
        this.cacheParams = cacheParams;

        //BEGIN_INCLUDE(init_memory_cache)
        // Set up memory cache
        if (this.cacheParams.memoryCacheEnabled) {

            /*
            If we're running on Honeycomb or newer, create a set of reusable bitmaps that can be
            populated into the inBitmap field of BitmapFactory.Options. Note that the set is
            of SoftReferences which will actually not be very effective due to the garbage
            collector being aggressive clearing Soft/WeakReferences. A better approach
            would be to use a strongly references bitmaps, however this would require some
            balancing of memory usage between this set and the bitmap LruCache. It would also
            require knowledge of the expected size of the bitmaps. From Honeycomb to JellyBean
            the size would need to be precise, from KitKat onward the size would just need to
            be the upper bound (due to changes in how inBitmap can re-use bitmaps).
            */
            if (Utils.hasHoneycomb()) {
                reusableBitmaps =
                        Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
            }

            memCache = new LruCache<String, BitmapDrawable>(ImageCache.this.cacheParams.memCacheSize) {

                /**
                 * Notify the removed entry that is no longer being cached
                 */
                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            BitmapDrawable oldValue, BitmapDrawable newValue) {
                    if (RecyclingBitmapDrawable.class.isInstance(oldValue)) {
                        // The removed entry is a recycling drawable, so notify it
                        // that it has been removed from the memory cache
                        ((RecyclingBitmapDrawable) oldValue).setIsCached(false);
                    } else {
                        // The removed entry is a standard BitmapDrawable

                        if (Utils.hasHoneycomb()) {
                            // We're running on Honeycomb or later, so add the bitmap
                            // to a SoftReference set for possible use with inBitmap later
                            reusableBitmaps.add(new SoftReference<Bitmap>(oldValue.getBitmap()));
                        }
                    }
                }

                /**
                 * Measure item size in kilobytes rather than units which is more practical
                 * for a bitmap cache
                 */
                @Override
                protected int sizeOf(String key, BitmapDrawable value) {
                    final int bitmapSize = getBitmapSize(value) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }
            };
        }
        //END_INCLUDE(init_memory_cache)
    }

    /**
     * Adds a bitmap to both memory and disk cache.
     *
     * @param data  Unique identifier for the bitmap to store
     * @param value The bitmap drawable to store
     */
    public void addBitmapToCache(String data, BitmapDrawable value) {
        if (data == null || value == null) {
            return;
        }

        // Add to memory cache
        if (memCache != null) {
            if (RecyclingBitmapDrawable.class.isInstance(value)) {
                // The removed entry is a recycling drawable, so notify it
                // that it has been added into the memory cache
                ((RecyclingBitmapDrawable) value).setIsCached(true);
            }
            memCache.put(data, value);
        }
    }

    /**
     * Get from memory cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap drawable if found in cache, null otherwise
     */
    public BitmapDrawable getBitmapFromMemCache(String data) {
        BitmapDrawable memValue = null;

        if (memCache != null) {
            memValue = memCache.get(data);
        }
        return memValue;
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    protected Bitmap getBitmapFromReusableSet(BitmapFactory.Options options) {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        Bitmap bitmap = null;

        if (reusableBitmaps != null && !reusableBitmaps.isEmpty()) {
            synchronized (reusableBitmaps) {
                final Iterator<SoftReference<Bitmap>> iterator = reusableBitmaps.iterator();
                Bitmap item;

                while (iterator.hasNext()) {
                    item = iterator.next().get();

                    if (null != item && item.isMutable()) {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item;

                            // Remove from reusable set so it can't be used again
                            iterator.remove();
                            break;
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove();
                    }
                }
            }
        }

        return bitmap;
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    /**
     * Clears both the memory cache associated with this ImageCache object. Note that
     * this includes disk access so this should not be executed on the main/UI thread.
     */
    public void clearCache() {
        if (memCache != null) {
            memCache.evictAll();
        }
    }

    /**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;

        /**
         *
         * @param percent
         */
        public ImageCacheParams(float percent) {
            setMemCacheSizePercent(percent);
        }

        /**
         * Sets the memory cache size based on a percentage of the max available VM memory.
         * Eg. setting percent to 0.2 would set the memory cache to one fifth of the available
         * memory. Throws {@link IllegalArgumentException} if percent is < 0.01 or > .8.
         * memCacheSize is stored in kilobytes instead of bytes as this will eventually be passed
         * to construct a LruCache which takes an int in its constructor.
         * <p/>
         * This value should be chosen carefully based on a number of factors
         * Refer to the corresponding Android Training class for more discussion:
         * http://developer.android.com/training/displaying-bitmaps/
         *
         * @param percent Percent of available app memory to use to size memory cache
         */
        public void setMemCacheSizePercent(float percentMemCached) {
            if (percentMemCached < 0.01f || percentMemCached > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                        + "between 0.01 and 0.8 (inclusive)");
            }
            memCacheSize = Math.round(percentMemCached * Runtime.getRuntime().maxMemory() / 1024);
        }
    }

    /**
     * @param candidate     - Bitmap to check
     * @param targetOptions - Options that have the out* value populated
     * @return true if <code>candidate</code> can be used for inBitmap re-use with
     * <code>targetOptions</code>
     */
    @TargetApi(VERSION_CODES.KITKAT)
    private static boolean canUseForInBitmap(
            Bitmap candidate, BitmapFactory.Options targetOptions) {
        //BEGIN_INCLUDE(can_use_for_inbitmap)
        if (!Utils.hasKitKat()) {
            // On earlier versions, the dimensions must match exactly and the inSampleSize must be 1
            return candidate.getWidth() == targetOptions.outWidth
                    && candidate.getHeight() == targetOptions.outHeight
                    && targetOptions.inSampleSize == 1;
        }

        // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
        // is smaller than the reusable bitmap candidate allocation byte count.
        int width = targetOptions.outWidth / targetOptions.inSampleSize;
        int height = targetOptions.outHeight / targetOptions.inSampleSize;
        int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
        return byteCount <= candidate.getAllocationByteCount();
        //END_INCLUDE(can_use_for_inbitmap)
    }

    /**
     * Return the byte usage per pixel of a bitmap based on its configuration.
     *
     * @param config The bitmap configuration.
     * @return The byte usage per pixel.
     */
    private static int getBytesPerPixel(Config config) {
        if (config == Config.ARGB_8888) {
            return 4;
        } else if (config == Config.RGB_565) {
            return 2;
        } else if (config == Config.ARGB_4444) {
            return 2;
        } else if (config == Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }

    /**
     * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat)
     * onward this returns the allocated memory size of the bitmap which can be larger than the
     * actual bitmap data byte count (in the case it was re-used).
     *
     * @param value
     * @return size in bytes
     */
    @TargetApi(VERSION_CODES.KITKAT)
    public static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();

        // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
        // larger than bitmap byte count.
        if (Utils.hasKitKat()) {
            return bitmap.getAllocationByteCount();
        }

        if (Utils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }

        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    @TargetApi(VERSION_CODES.GINGERBREAD)
    public static long getUsableSpace(File path) {
        if (Utils.hasGingerbread()) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }
}

