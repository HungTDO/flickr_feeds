package com.framgia.flickrfeeds.util;

import android.app.Activity;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by quannh on 1/8/15.
 */
public class Utils {
    private static final String TAG = "Util";

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    /**
     * Get longest display screen.
     *
     * @param activity
     * @return
     */
    public static int getLongestDisplay(Activity activity) {
        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        final int longest = (height > width ? height : width) / 2;

        return longest;
    }

    /**
     * Convert time from millisecond to date.
     * @param timeStamp mililseconds since Jan 1, 1970 GMT
     * @return time format HH:mm:ss\nyyyy dd MMM
     */
    public static String formatTimestamp(String timeStamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss\nyyyy dd MMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        return formatter.format(calendar.getTime());
    }

    /**
     * Convert file size from byte to human readable size.
     * @param bytes
     * @return
     */
    public static String convertByteToHumanReadable(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp-1) + ("i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }
}

