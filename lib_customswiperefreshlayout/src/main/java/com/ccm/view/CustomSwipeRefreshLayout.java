package com.ccm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 自定义下拉刷新
 */
public class CustomSwipeRefreshLayout extends ViewGroup {
    private int mRefreshModel;

    private View mContent;//内容视图
    private View mRefreshView;//下拉刷新视图
    private boolean isRefreshing;//刷新中
    private Slider mSlider;//实际控制滑动对象
    private float mDownX;//Action_down x坐标
    private float mDownY;//Action_down y坐标
    private RefreshListener mListener;

    private enum interceptType {
        intercept,
        no_intercept,
        none
    }

    private interceptType mInterceptThisSoler;
    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);

    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSwipeRefreshLayout);
        mRefreshModel = typedArray.getInt(R.styleable.CustomSwipeRefreshLayout_refreshMode, 0);
    }

    public void setmContent(View contentView) {
        this.mContent = contentView;
        ViewGroup.LayoutParams layoutParams = mContent.getLayoutParams();
        if (layoutParams == null) {
            mContent.setLayoutParams(generateDefaultLayoutParams());
        }
        if (layoutParams instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams) layoutParams;
            lp.mRefreshLayout = 0;
        } else {
            throw new RuntimeException("LayoutParams must CustomSwipeRefreshLayout.LayoutParams");
        }
        this.addView(mContent);
    }

    public View getContentView() {
        return mContent;
    }

    public void setmRefreshView(View refreshView) {
        this.mRefreshView = refreshView;
        ViewGroup.LayoutParams layoutParams = mRefreshView.getLayoutParams();
        if (layoutParams == null) {
            mRefreshView.setLayoutParams(generateDefaultLayoutParams());
        }
        if (layoutParams instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams) layoutParams;
            lp.mRefreshLayout = 1;
        } else {
            throw new RuntimeException("LayoutParams must CustomSwipeRefreshLayout.LayoutParams");
        }
        this.addView(mRefreshView);
    }

    public View getRefreshView() {
        return mRefreshView;
    }

    public void setListener(RefreshListener listener) {
        this.mListener = listener;
        if (mSlider != null) {
            mSlider.setRefreshListener(this.mListener);
        }
    }

    public RefreshListener getListener() {
        return mListener;
    }

    private void init() {
        int children = getChildCount();
        if (children == 0) {
            return;
        }
        if (children > 2) {
            throw new RuntimeException("children view most 2");
        }

        if (children == 1) {
            View view = getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams.mRefreshLayout != 1) {
                throw new RuntimeException("No drop-down layout");
            }
        } else if (children == 2) {
            View view = getChildAt(0);
            View view1 = getChildAt(1);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            LayoutParams layoutParams1 = (LayoutParams) view1.getLayoutParams();
            if (layoutParams.mRefreshLayout == layoutParams1.mRefreshLayout) {
                throw new RuntimeException("'freshLayout' not the same");
            }
            if (layoutParams.mRefreshLayout == 0) {
                mContent = view;
            } else {
                mRefreshView = view;
            }
            if (layoutParams1.mRefreshLayout == 0) {
                mContent = view1;
            } else {
                mRefreshView = view1;
            }
        }
        mSlider = SliderFactory.createSlider(mRefreshModel);
        if (this.mListener != null) {
            mSlider.setRefreshListener(this.mListener);
        }
        mSlider.setContentLayout(mContent);
        mSlider.setRefreshLayout(mRefreshView);
        mSlider.setParantLayout(this);
    }


    /**
     * 判断content 是否可以向上滑动
     * @return
     */
    protected boolean canChildScrollUp() {
        if (mContent instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mContent, -1);
        }
        return mContent.canScrollVertically(-1);
    }


    /**
     * 设置刷新状态
     * @param refreshing
     */
    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
        mSlider.startScroll(isRefreshing, 600);
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mSlider == null) {
            init();
        }
        if (mRefreshView != null) {
            measureChild(mRefreshView, widthMeasureSpec, widthMeasureSpec);
        }
        if (mContent != null) {
            measureChild(mContent, widthMeasureSpec, heightMeasureSpec);
        }
        if (mContent != null) {
            int wMode = MeasureSpec.getMode(widthMeasureSpec);
            int hMode = MeasureSpec.getMode(heightMeasureSpec);
            if (wMode == MeasureSpec.AT_MOST || hMode == MeasureSpec.AT_MOST) {
                int wSize = MeasureSpec.getSize(widthMeasureSpec);
                int hSize = MeasureSpec.getSize(heightMeasureSpec);
                if (wMode == MeasureSpec.AT_MOST) {
                    int cWidth = mContent.getMeasuredWidth();
                    if (wSize > cWidth) {
                        wSize = cWidth;
                    }
                    int cHeight = mContent.getMeasuredHeight();
                    if (hSize > cHeight) {
                        hSize = cHeight;
                    }
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(wSize, wMode);
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(hSize, hMode);
                    setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
                }
            } else {
                //测量自己
                setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight(), lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom(), lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);

        int size = Math.max(0, specSize - padding);

        int resultSize = 0;
        int resultMode = 0;

        switch (specMode) {
            // Parent has imposed an exact size on us
            case MeasureSpec.EXACTLY:
                if (childDimension >= 0) {
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size. So be it.
                    resultSize = size;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = MeasureSpec.AT_MOST;
                }
                break;

            // Parent has imposed a maximum size on us
            case MeasureSpec.AT_MOST:
                if (childDimension >= 0) {
                    // Child wants a specific size... so be it
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size, but our size is not fixed.
                    // Constrain child to not be bigger than us.
                    resultSize = size;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size. It can't be
                    // bigger than us.
                    resultSize = size;
                    resultMode = MeasureSpec.AT_MOST;
                }
                break;

            // Parent asked to see how big we want to be
            case MeasureSpec.UNSPECIFIED:
                if (childDimension >= 0) {
                    // Child wants a specific size... let him have it
                    resultSize = childDimension;
                    resultMode = MeasureSpec.EXACTLY;
                } else if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                    // Child wants to be our size... find out how big it should
                    // be
                    resultSize = size;
                    resultMode = MeasureSpec.UNSPECIFIED;
                } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    // Child wants to determine its own size.... find out how
                    // big it should be
                    resultSize = size;
                    resultMode = MeasureSpec.UNSPECIFIED;
                }
                break;
        }
        //noinspection ResourceType
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mSlider == null) {
            init();
        }
        if (mSlider != null) {
            mSlider.onLayout(l + getPaddingLeft(), r - getPaddingRight(), t + getPaddingTop(), b - getPaddingBottom());
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mRefreshView == null) {
            init();
        }
        if (mContent == null || mRefreshView == null) {
            return false;
        }
        if (isRefreshing) {
            return true;
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                mInterceptThisSoler = interceptType.none;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (mInterceptThisSoler == interceptType.none) {
                    int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                    double slop = getTheDistance(mDownX, mDownY, ev.getX(), ev.getY());
                    if (slop >= touchSlop) {
                        float absX = getAbsDistance(mDownX, ev.getX());
                        float absY = getAbsDistance(mDownY, ev.getY());
                        if (absX > absY) {
                            mInterceptThisSoler = interceptType.no_intercept;
                        } else if (ev.getY() > mDownY && !canChildScrollUp()) {
                            mInterceptThisSoler = interceptType.intercept;
                        } else {
                            mInterceptThisSoler = interceptType.no_intercept;
                        }
                    }
                }
                if (mInterceptThisSoler == interceptType.intercept) {
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRefreshing) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mSlider.onTouchDown(event);

                break;
            case MotionEvent.ACTION_MOVE:
                if (mInterceptThisSoler == interceptType.intercept) {
                    mSlider.scroll(event.getY() - mDownY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mInterceptThisSoler == interceptType.intercept) {
                    int heght = mRefreshView.getMeasuredHeight();
                    if (event.getY() - mDownY >= (heght / 2)) {
                        setRefreshing(true);
                    } else {
                        setRefreshing(false);
                    }
                }
                mInterceptThisSoler = interceptType.none;
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        mSlider.computeScroll();
    }

    /**
     * 计算2点距离
     *
     * @param oldx
     * @param oldy
     * @param newX
     * @param newY
     * @return
     */
    private double getTheDistance(float oldx, float oldy, float newX, float newY) {
        return Math.sqrt(Math.pow((newX - oldx), 2) + Math.pow(newY - oldy, 2));
    }

    /**
     * 计算2个值的距离觉得值
     *
     * @param one
     * @param tow
     * @return
     */
    private float getAbsDistance(float one, float tow) {
        return Math.abs(tow - one);
    }


    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.mRefreshLayout = 0;
        return layoutParams;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p == null) {
            return false;
        }
        return p instanceof LayoutParams;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return (LayoutParams) p;
        }
        return new LayoutParams(p.width, p.height);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int mRefreshLayout;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            getRefreshlayout(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        private void getRefreshlayout(Context context, AttributeSet attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSwipeRefreshLayout);
            mRefreshLayout = typedArray.getInt(R.styleable.CustomSwipeRefreshLayout_freshLayout, 0);
        }
    }

    public interface Slider {

        void setRefreshLayout(View view);

        void setContentLayout(View view);

        void setParantLayout(View view);

        void onLayout(int left, int right, int top, int bottom);

        void onTouchDown(MotionEvent event);

        void scroll(float y);

        void startScroll(boolean reFreshing, int time);

        void computeScroll();

        void setRefreshListener(RefreshListener listener);
    }

    public interface RefreshListener {
        void onFresh(boolean b);

        /**
         * 下拉进度，1-100
         *
         * @param p
         */
        void progress(int p);
    }
}
