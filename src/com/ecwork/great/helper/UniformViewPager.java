package com.ecwork.great.helper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * User: ecsark
 * Date: 2/13/14
 * Time: 3:00 PM
 */
public class UniformViewPager extends ViewPager {

    // we name the left, middle and right page
    private static final int PAGE_LEFT = 0;
    private static final int PAGE_MIDDLE = 1;
    private static final int PAGE_RIGHT = 2;

    private int mSelectedPageIndex = 1;

    public ArrayList<Integer> getDynamicPages() {
        return dynamicPages;
    }

    private ArrayList<Integer> dynamicPages;

    public UniformViewPager(Context context) {
        super(context);
    }

    public UniformViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize() {
        initialize(0);
    }

    public void initialize(int position) {

        dynamicPages = new ArrayList<Integer>();
        for (int i=position; i<position+3; ++i) {
            dynamicPages.add(i);
        }

        setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex = position;
                int a = 10;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {

                    int oLeft = dynamicPages.get(PAGE_LEFT);
                    int oMid = dynamicPages.get(PAGE_MIDDLE);
                    int oRight = dynamicPages.get(PAGE_RIGHT);

                    // user swiped to right direction --> left page
                    if (mSelectedPageIndex == PAGE_LEFT) {

                        // moving each page content one page to the right
                        dynamicPages.set(PAGE_LEFT, oLeft - 1);
                        dynamicPages.set(PAGE_MIDDLE, oLeft);
                        dynamicPages.set(PAGE_RIGHT, oMid);

                        // user swiped to left direction --> right page
                    } else if (mSelectedPageIndex == PAGE_RIGHT) {

                        dynamicPages.set(PAGE_LEFT, oMid);
                        dynamicPages.set(PAGE_MIDDLE, oRight);
                        dynamicPages.set(PAGE_RIGHT, oRight + 1);

                    }

                    // refresh pages
                    getAdapter().notifyDataSetChanged();

                    setCurrentItem(PAGE_MIDDLE, false);
                }
            }
        });
       
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // find the first child view
        View view = getChildAt(0);
        if (view != null) {
            // measure the first child view with the specified measure spec
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, view));
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @param view the base view with already measured height
     *
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec, View view) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // set the height from the base view if available
            if (view != null) {
                result = view.getMeasuredHeight();
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}
