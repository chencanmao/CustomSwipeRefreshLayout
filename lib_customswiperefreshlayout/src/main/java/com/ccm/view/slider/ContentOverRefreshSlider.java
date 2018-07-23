package com.ccm.view.slider;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.ccm.view.CustomSwipeRefreshLayout;

/**
 * 内容布局下拉，显露位于内容布局下方的刷新布局
 */
public class ContentOverRefreshSlider implements CustomSwipeRefreshLayout.Slider {
    private View mContentView;
    private View mRefreshView;
    private View mParentVeiw;
    private Scroller mScroller;
    private int mDistance;
    private boolean isCanScrol;
    private CustomSwipeRefreshLayout.RefreshListener mListener;


    @Override
    public void setRefreshLayout(View view) {
        this.mRefreshView = view;

    }
    public void setRefreshListener(CustomSwipeRefreshLayout.RefreshListener listener) {
        this.mListener = listener;
    }

    @Override
    public void setContentLayout(View view) {
        this.mContentView = view;
        if (mContentView != null) {
            //如果content没有设置背景，默认白色
            Drawable background = mContentView.getBackground();
            if(background == null) {
                mContentView.setBackgroundColor(Color.WHITE);
            }else if(background instanceof ColorDrawable) {
                ColorDrawable cb = (ColorDrawable) background;
                if(cb.getColor() == Color.TRANSPARENT) {
                    mContentView.setBackgroundColor(Color.WHITE);
                }
            }
        }
        if (mContentView != null) {
            mContentView.bringToFront();
        }
    }

    public void setParantLayout(View view) {
        mParentVeiw = view;
    }

    @Override
    public void onLayout(int left, int right, int top, int bottom) {
        int measureWidth = mContentView.getMeasuredWidth();
        int measureHeight = mContentView.getMeasuredHeight();
        CustomSwipeRefreshLayout.LayoutParams layoutParams = (CustomSwipeRefreshLayout.LayoutParams) mContentView.getLayoutParams();
        mContentView.layout(left + layoutParams.leftMargin, top + layoutParams.topMargin + mDistance, left + measureWidth + layoutParams.leftMargin, top + measureHeight + layoutParams.topMargin + mDistance);
        CustomSwipeRefreshLayout.LayoutParams layoutParams1 = (CustomSwipeRefreshLayout.LayoutParams) mRefreshView.getLayoutParams();
        int measuredHeight = mRefreshView.getMeasuredHeight();
        int measuredWidth = mRefreshView.getMeasuredWidth();
        int height = (layoutParams1.topMargin + measuredHeight);
        mRefreshView.layout(left + layoutParams1.leftMargin, top + layoutParams1.topMargin, left + measuredWidth + layoutParams1.leftMargin, top + height);
    }

    @Override
    public void onTouchDown(MotionEvent event) {
        mScroller = new Scroller(mContentView.getContext());
        isCanScrol = false;

    }

    @Override
    public void scroll(float y) {
        if (mRefreshView == null ) {
            return;
        }
        int measuredHeight = mRefreshView.getMeasuredHeight();
        CustomSwipeRefreshLayout.LayoutParams layoutParams = (CustomSwipeRefreshLayout.LayoutParams) mRefreshView.getLayoutParams();
        measuredHeight += layoutParams.topMargin + layoutParams.bottomMargin;
        if (y < 0) {
            mDistance = 0;
        } else if (y > measuredHeight) {
            mDistance = measuredHeight;
        } else {
            mDistance = (int) y;
        }
        if (mListener != null) {
            float percent = (mDistance/measuredHeight) * 100;
            mListener.progress((int) percent);
        }
        mParentVeiw.requestLayout();
    }

    public void computeScroll() {
        if (isCanScrol && mScroller.computeScrollOffset()) {
            scroll(mScroller.getCurrY());
            mParentVeiw.postInvalidate();
        }
    }

    @Override
    public void startScroll(boolean reFreshing, int time) {
        if (mScroller == null) {
            mScroller = new Scroller(mContentView.getContext());
        }
        if (mListener != null) {
            mListener.onFresh(reFreshing);
        }
        CustomSwipeRefreshLayout.LayoutParams layoutParams = (CustomSwipeRefreshLayout.LayoutParams) mRefreshView.getLayoutParams();
        isCanScrol = true;
        if (reFreshing) {
            int distance = layoutParams.bottomMargin + layoutParams.topMargin + mRefreshView.getMeasuredHeight() - mDistance;
            if (distance > 0) {
                mScroller.startScroll((int) 0, mDistance, 0, distance, time);
            }
        } else {
            if (mDistance > 0) {
                mScroller.startScroll((int) 0, mDistance, 0, -mDistance, time);
            }
        }
        mParentVeiw.postInvalidate();
    }
}
