package com.ccm.view;

import com.ccm.view.slider.ContentAndRefreshDownSlider;
import com.ccm.view.slider.ContentOverRefreshSlider;
import com.ccm.view.slider.RefreshHalfDownSlider;
import com.ccm.view.slider.RefreshOverContentSlider;

class SliderFactory {
    public static CustomSwipeRefreshLayout.Slider createSlider(int mode) {
        CustomSwipeRefreshLayout.Slider mSlider = null;
        switch (mode) {
            case 0:
                mSlider = new ContentAndRefreshDownSlider();
                break;
            case 1:
                mSlider = new RefreshOverContentSlider();
                break;
            case 2:
                mSlider = new ContentOverRefreshSlider();
                break;
            case 3:
                mSlider = new RefreshHalfDownSlider();
                break;
            default:
                mSlider = new ContentAndRefreshDownSlider();
                break;
        }
        return mSlider;
    }
}
