package com.framgia.flickrfeeds.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.framgia.flickrfeeds.R;
import com.framgia.flickrfeeds.core.BaseImage;
import com.framgia.flickrfeeds.ui.GridImage.OnFragmentInteractionListener;

import java.util.List;

/**
 * Created by quannh on 1/14/15.
 */
public abstract class SortedGridActivity extends ActionBarActivity
        implements OnFragmentInteractionListener, OnNavigationListener {
    protected ActionBar actionBar;
    protected FragmentManager fragmentManager;

    protected String activityTitle;
    protected BaseImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_picker);

        activityTitle = getResources().getString(R.string.app_name);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            image = bundle.getParcelable(AlbumDetail.ALBUM);
            activityTitle = image.getBucketName();
        } else {
            image = new BaseImage();
        }

        actionBar = getSupportActionBar();
        fragmentManager = getSupportFragmentManager();

        createActionbarDropdownMenu();
    }

    /**
     * @return
     */
    protected abstract int getNumberOfColumns();

    /**
     * @return
     */
    protected abstract String getDefaultSortType();


    private void createActionbarDropdownMenu() {
        SpinnerAdapter menuAdapter = new AdapterBaseMaps(this, getSortTypeList());

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(menuAdapter, this);
    }

    protected abstract List<String> getSortTypeList();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        return onSortTypeChange(position);
    }

    protected boolean onSortTypeChange(int position) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        GridImage.newInstance(getNumberOfColumns(), getViewType(), getAlbumId(),
                                getSortTypeByPosition(position)))
                .commit();
        return true;
    }

    /**
     * @param position
     * @return
     */
    protected abstract String getSortTypeByPosition(int position);

    protected abstract int getViewType();

    protected abstract String getAlbumId();

    public class AdapterBaseMaps extends BaseAdapter {

        Context context;
        List<String> subtitleList;
        LayoutInflater inflater;

        public AdapterBaseMaps(Context context, List<String> subtitleList) {
            this.context = context;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.subtitleList = subtitleList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View actionBarView = inflater.inflate(R.layout.ab_main_view, parent, false);
            TextView title = (TextView) actionBarView.findViewById(R.id.ab_title);
            TextView subtitle = (TextView) actionBarView.findViewById(R.id.ab_subtitle);

            title.setText(activityTitle);
            subtitle.setText(subtitleList.get(position));

            return actionBarView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View actionBarDropDownView = inflater.inflate(R.layout.ab_dropdown_view, parent, false);
            TextView dropDownTitle = (TextView) actionBarDropDownView.findViewById(R.id.ab_dropdown_title);

            dropDownTitle.setText(subtitleList.get(position));

            return actionBarDropDownView;
        }

        @Override
        public int getCount() {
            return subtitleList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // TODO update fragment
    }
}
