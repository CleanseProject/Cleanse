package com.cleanseproject.cleanse.fragments.mapFragment;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Map wrapper class
 */
public class MapWrapperLayout extends FrameLayout {

    private OnDragListener mOnDragListener;

    /**
     * Contructor calling super
     * @param context Current context
     */
    public MapWrapperLayout(Context context) {
        super(context);
    }

    /**
     * Called when map is moved by user
     */
    public interface OnDragListener {
        void onDrag(MotionEvent motionEvent);
    }

    /**
     * Calls  super method when listener is set
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (mOnDragListener != null) {
            mOnDragListener.onDrag(motionEvent);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /**
     * @param onDragListener OnDragListener to be set
     */
    public void setOnDragListener(OnDragListener onDragListener) {
        this.mOnDragListener = onDragListener;
    }
}
