package com.framgia.flickrfeeds.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.flickrfeeds.R;
import com.framgia.flickrfeeds.core.Album;

import java.util.List;

/**
 * Created by quannh on 1/8/15.
 */
public class GridImageAdapter extends BaseAdapter {
    private final static String TAG = "GridImageAdapter";

    private List<Album> images;
    private LayoutInflater inflater;

    public GridImageAdapter(Context context, List<Album> images) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.images = images;
    }

    public void addItem(Album image) {
        images.add(image);
    }

    public void updateDisplay() {
        notifyDataSetChanged();
    }

    public void clear() {
        images.clear();
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
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        Album item = images.get(position);

        ImageView thumb = (ImageView) convertView.findViewById(R.id.thumbnail);
        thumb.setImageBitmap(item.getThumb());

        viewHolder.title.setText(item.getName());

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
