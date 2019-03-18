package com.cleanseproject.cleanse.fragments.mapFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Project specific Google Map class
 */
public class CleanseMapFragment extends SupportMapFragment {

    private View mOriginalView;
    private MapWrapperLayout mMapWrapperLayout;

    /**
     * Initializes class
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOriginalView = super.onCreateView(inflater, container, savedInstanceState);
        mMapWrapperLayout = new MapWrapperLayout(getActivity());
        mMapWrapperLayout.addView(mOriginalView);
        return mMapWrapperLayout;
    }

    /**
     * @return Original view
     */
    @Override
    public View getView() {
        return mOriginalView;
    }

    /**
     * @param onDragListener Listener to be called when map is moved by user
     */
    public void setOnDragListener(MapWrapperLayout.OnDragListener onDragListener) {
        mMapWrapperLayout.setOnDragListener(onDragListener);
    }

}