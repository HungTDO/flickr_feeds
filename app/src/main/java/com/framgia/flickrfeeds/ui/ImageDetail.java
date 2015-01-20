package com.framgia.flickrfeeds.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.flickrfeeds.R;
import com.framgia.flickrfeeds.core.BaseImage;
import com.framgia.flickrfeeds.core.ImageLoader;
import com.framgia.flickrfeeds.core.ImageLoader.LoaderListener;
import com.framgia.flickrfeeds.util.Utils;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.framgia.flickrfeeds.ui.ImageDetail.OnImageTapListener} interface
 * to handle interaction events.
 * Use the {@link ImageDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageDetail extends Fragment implements LoaderListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGE = "image";

    private BaseImage image;

    private OnImageTapListener listener;

    private View rootView;
    private ImageView imageView;
    private PhotoViewAttacher attacher;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param image
     * @return
     */
    public static ImageDetail newInstance(BaseImage image) {
        ImageDetail fragment = new ImageDetail();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image = getArguments().getParcelable(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.image_detail, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.image);

        // Display image with full size
        int imageSize = Utils.getLongestDisplay(getActivity());
        ImageLoader.getInstance().setImageSize(imageSize).displayImage(image.getDataUri(),
                imageView, this);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        attacher = new PhotoViewAttacher(imageView);

        // Toggle footer control when on tap to image.
        attacher.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (isAdded())
                    listener.onFragmentInteraction();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnImageTapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onLoaderComplete() {
        // update attacher
        attacher.update();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnImageTapListener {
        public void onFragmentInteraction();
    }

}
