package com.moran.spiceitapp;

import android.view.MotionEvent;
import android.view.View;

public class OnTouchClickListener implements View.OnTouchListener {

    private int minMove = 5;
    private float startX;
    private float startY;
    private OnClickListener mListener;

    public OnTouchClickListener(OnClickListener mListener, int minMove) {
        this.mListener = mListener;
        this.minMove = minMove;
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > minMove || differenceY > minMove) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (isAClick(startX, endX, startY, endY)) {
                    mListener.onClick(v);
                }
                break;
        }
        return false;
    }

    public interface OnClickListener {
        void onClick(View v);
    }
}
