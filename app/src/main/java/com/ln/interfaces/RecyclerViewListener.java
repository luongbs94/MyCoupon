package com.ln.interfaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Nhahv on 7/23/2016.
 * <></>
 */
public class RecyclerViewListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector mGestureDetector;
    private OnClickRecyclerView mOnClick;

    public RecyclerViewListener(Context context, OnClickRecyclerView onClick) {
        mOnClick = onClick;
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                    }
                });
    }


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && mOnClick != null && mGestureDetector.onTouchEvent(e)) {
            mOnClick.onClick(child, rv.getChildPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnClickRecyclerView {
        void onClick(View view, int position);
    }

}
