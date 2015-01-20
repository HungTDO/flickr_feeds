package com.framgia.flickrfeeds.ui;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.flickrfeeds.R;
import com.framgia.flickrfeeds.core.Album;
import com.framgia.flickrfeeds.core.BaseImage;
import com.framgia.flickrfeeds.core.ImageLoader;
import com.framgia.flickrfeeds.ui.ImageDetail.OnImageTapListener;
import com.framgia.flickrfeeds.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageViewPager extends FragmentActivity implements OnImageTapListener, OnClickListener, OnPageChangeListener {

    public static final String ALBUM = "ImageDetail.album";
    public static final String POSITION = "ImageDetail.position";

    private ViewPager pager;
    private PagerAdapter adapter;

    private View footerControl;
    private ImageView detailBtn;
    private View imageDetail;
    private ImageView shareBtn;
    private ImageView setBackgroundBtn;

    protected Album album;
    protected int position;

    private int shortAnimTime = 200;

    private boolean isVisible = true;
    private boolean isActivated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            album = bundle.getParcelable(ImageViewPager.ALBUM);
            position = bundle.getInt(ImageViewPager.POSITION);
        } else {
            album = new Album(new ArrayList<BaseImage>());
        }
        setContentView(R.layout.image_viewpager);

        pager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ImagePagerAdapter(getSupportFragmentManager(), album.getImageList());
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        pager.setOnPageChangeListener(this);

        // Locate all footer view
        footerControl = findViewById(R.id.footer_controls);
        detailBtn = (ImageView) findViewById(R.id.detail);
        imageDetail = findViewById(R.id.image_detail);
        shareBtn = (ImageView) findViewById(R.id.share);
        setBackgroundBtn = (ImageView) findViewById(R.id.set_background);

        footerControl.setOnClickListener(this);
        detailBtn.setOnClickListener(this);
        imageDetail.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        setBackgroundBtn.setOnClickListener(this);

        shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public void onFragmentInteraction() {
        int controlsHeight = footerControl.getHeight();
        if (isActivated) {
            toggleImageDetail();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            footerControl.animate()
                    .translationY(isVisible ? controlsHeight : 0)
                    .setDuration(shortAnimTime);
        } else {
            footerControl.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        isVisible = !isVisible;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.footer_controls:
                break;
            case R.id.detail:
                toggleImageDetail();
                break;
            case R.id.share:
                shareImageViaIntent();
                break;
            case R.id.set_background:
                openSetWallpaperDialog();
                break;
            default:
                break;
        }
    }


    /**
     * Toggle image detail view.
     */
    private void toggleImageDetail() {
        if (!isActivated) setImageDetail();
        imageDetail.setVisibility(isActivated ? View.INVISIBLE : View.VISIBLE);

        detailBtn.setImageDrawable(isActivated ? getResources().getDrawable(R.drawable.ic_action_collapse)
                : getResources().getDrawable(R.drawable.ic_action_expand));
        detailBtn.setPadding(10, 10, 10, 10);
        isActivated = !isActivated;
    }

    /**
     * Bind image details to view.
     */
    private void setImageDetail() {
        BaseImage item = album.getImageList().get(pager.getCurrentItem());

        TextView time = (TextView) findViewById(R.id.time);
        TextView imageSize = (TextView) findViewById(R.id.image_size);
        TextView fileSize = (TextView) findViewById(R.id.file_size);
        TextView path = (TextView) findViewById(R.id.path);

        time.setText(Utils.formatTimestamp(item.getDateTaken()));
        setImageSize(item, imageSize);
        fileSize.setText("File size: " + Utils.convertByteToHumanReadable(Long.parseLong(item.getSize())));
        path.setText("Path: " + item.getDataUri());
    }

    /**
     * Fetch image width and height by decode bitmap.
     * 
     * @param item
     * @param imageSize
     */
    private void setImageSize(BaseImage item, TextView imageSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(item.getDataUri().toString(), options);
        int width = options.outWidth;
        int height = options.outHeight;

        imageSize.setText("Image size: " + width + "x" + height);
    }

    /**
     * Share image.
     */
    private void shareImageViaIntent() {
        BaseImage item = album.getImageList().get(pager.getCurrentItem());

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, item.getDataUri());
        shareIntent.setType(item.getMimeType());
        startActivity(Intent.createChooser(shareIntent, "Share"));
    }

    private void openSetWallpaperDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Set Wallpaper")
                .setMessage("Are you sure you want to set this image as wallpaper?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setImageAsWallpaper();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setImageAsWallpaper() {
        BaseImage item = album.getImageList().get(pager.getCurrentItem());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(item.getDataUri().toString(), options);

        // Calculate inSampleSize
        options.inSampleSize = ImageLoader.calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap decodedSampleBitmap = BitmapFactory.decodeFile(item.getDataUri().toString(), options);

        WallpaperManager wm = WallpaperManager.getInstance(this);
        try {
            wm.setBitmap(decodedSampleBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setImageDetail();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Adapter for view pager.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final List<BaseImage> imageList;

        public ImagePagerAdapter(FragmentManager fm, List<BaseImage> imageList) {
            super(fm);
            this.imageList = imageList;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetail.newInstance(imageList.get(position));
        }

        @Override
        public int getCount() {
            return imageList.size();
        }
    }
}
