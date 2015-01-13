package com.framgia.flickrfeeds.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.framgia.flickrfeeds.R;
import com.framgia.flickrfeeds.core.AlbumManager;
import com.framgia.flickrfeeds.core.BaseImage;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridImage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridImage#newInstance} factory method to
 * create an instance of this fragment.
 * Created by quannh on 1/9/15.
 */
public class GridImage extends Fragment implements OnItemClickListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String NUMBER_OF_COLUMNS = "numberOfColumns";
    private static final String ALBUM_ID = "albumId";
    private static final String VIEW_TYPE = "viewType";
    private static final String ORDER_BY = "orderBy";

    public static final int GALLERY_VIEW = 0;
    public static final int ALBUM_VIEW = 1;

    private int numberOfColumns = 1;
    private String albumId;
    private int viewType = 0;
    private String orderBy;

    private OnFragmentInteractionListener mListener;

    // Root view
    private View rootView;

    private GridView gridImage;
    private GridImageAdapter adapter;

    private List<BaseImage> listItem;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param numberOfColumns Number of columns.
     * @return A new instance of fragment GridImage.
     */
    // TODO: Rename and change types and number of parameters
    public static GridImage newInstance(int numberOfColumns, int viewType, String albumId, String orderBy) {
        GridImage fragment = new GridImage();
        Bundle args = new Bundle();
        args.putInt(NUMBER_OF_COLUMNS, numberOfColumns);
        args.putInt(VIEW_TYPE, viewType);
        args.putString(ALBUM_ID, albumId);
        args.putString(ORDER_BY, orderBy);

        fragment.setArguments(args);
        return fragment;
    }

    public GridImage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            numberOfColumns = getArguments().getInt(NUMBER_OF_COLUMNS);
            viewType = getArguments().getInt(VIEW_TYPE);
            if (viewType != GALLERY_VIEW) {
                albumId = getArguments().getString(ALBUM_ID);
            }
            orderBy = getArguments().getString(ORDER_BY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_grid_image, container, false);

        // locate view from xml file
        locateView();

        // bind data to view
        bindView();

        return rootView;
    }

    /**
     * Locate view from UI file
     */
    private void locateView() {
        gridImage = (GridView) rootView.findViewById(R.id.grid_image);
    }

    /**
     * Bind data to view
     */
    private void bindView() {
        gridImage.setNumColumns(numberOfColumns);

        listItem = new ArrayList<>();

        // Get image list from sdcard
        if (viewType == GALLERY_VIEW) {
            listItem = AlbumManager.getAlbumList(getActivity().getContentResolver(), AlbumManager.PROJECTION,
                    AlbumManager.GALLERY_SELECTION, null, orderBy);
        } else if (viewType == ALBUM_VIEW) {
            listItem = AlbumManager.getAlbumList(getActivity().getContentResolver(), AlbumManager.PROJECTION,
                    AlbumManager.ALBUM_SELECTION, new String[] {albumId}, orderBy);
        }

        // setup gridview
        adapter = new GridImageAdapter(getActivity(), listItem);
        if (viewType == GALLERY_VIEW) adapter.setGallery(true);

        gridImage.setAdapter(adapter);
        gridImage.setOnItemClickListener(this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getActivity() instanceof GalleryPicker) {
            Intent intent = new Intent(getActivity(), AlbumDetail.class);
            intent.putExtra(AlbumDetail.ALBUM, listItem.get(position));
            getActivity().startActivity(intent);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
