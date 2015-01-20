package com.framgia.flickrfeeds.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.flickrfeeds.R;
import com.framgia.flickrfeeds.core.BaseImage;
import com.framgia.flickrfeeds.core.ImageLoader;

import java.util.List;

/**
 * Created by quannh on 1/8/15.
 */
public class GridImageAdapter extends BaseAdapter {
    private final static String TAG = "GridImageAdapter";

    private List<BaseImage> images;
    private Context context;

    private boolean isGallery = false;

    public GridImageAdapter(Context context, List<BaseImage> images) {
        this.context = context;
        this.images = images;
    }

    public void updateDisplay() {
        notifyDataSetChanged();
    }

    public void setGallery(boolean isGallery) {
        this.isGallery = isGallery;
    }


    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        BaseImage item = images.get(position);
        int imageSize = context.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        ImageView thumb = (ImageView) convertView.findViewById(R.id.thumbnail);
        ImageLoader.getInstance().setImageSize(imageSize, imageSize).displayImage(item.getDataUri(), thumb);

        if (isGallery) viewHolder.title.setText(item.getBucketName());
        else viewHolder.title.setVisibility(View.GONE);
        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        static TextView title;

        ViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.title);
        }
    }
}
