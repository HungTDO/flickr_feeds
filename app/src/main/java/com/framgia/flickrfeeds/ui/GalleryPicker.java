package com.framgia.flickrfeeds.ui;

import com.framgia.flickrfeeds.R;
import com.framgia.flickrfeeds.core.AlbumManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GalleryPicker extends SortedGridActivity {
    private static final String TAG = GalleryPicker.class.getSimpleName();

    protected int NUMBER_OF_COLUMN = 2;

    @Override
    protected int getNumberOfColumns() {
        return NUMBER_OF_COLUMN;
    }

    @Override
    protected String getDefaultSortType() {
        return AlbumManager.DATETAKEN_ORDER_BY_DESC;
    }

    @Override
    protected List<String> getSortTypeList() {
        return new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.gallery_sort_type)));
    }

    @Override
    protected String getSortTypeByPosition(int position) {
        switch (position) {
            case 0:
                return AlbumManager.DATETAKEN_ORDER_BY_DESC;
            case 1:
                return AlbumManager.DATETAKEN_ORDER_BY_ASC;
            case 2:
                return AlbumManager.BUCKET_NAME_ORDER_BY;
            default:
                return AlbumManager.DATETAKEN_ORDER_BY_DESC;
        }
    }

    @Override
    protected int getViewType() {
        return GridImage.GALLERY_VIEW;
    }

    @Override
    protected String getAlbumId() {
        return new String();
    }
}
