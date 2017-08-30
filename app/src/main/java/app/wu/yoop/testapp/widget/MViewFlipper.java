package app.wu.yoop.testapp.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import app.wu.yoop.testapp.R;

/**
 * Created by wuyan on 2017/7/9 0009.
 */
public class MViewFlipper extends ViewFlipper implements MGestureListener.OnFlingListener {

    private GestureDetector mGestureDetector = null;

    private OnViewFlipperListener mOnViewFlipperListener = null;

    private boolean isFirstPage;
    private boolean isLastPage;

    public MViewFlipper(Context context) {
        super(context);
    }

    public MViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnViewFlipperListener(OnViewFlipperListener mOnViewFlipperListener) {
        this.mOnViewFlipperListener = mOnViewFlipperListener;
        MGestureListener myGestureListener = new MGestureListener();
        myGestureListener.setOnFlingListener(this);
        mGestureDetector = new GestureDetector(myGestureListener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (null != mGestureDetector) {
            return mGestureDetector.onTouchEvent(ev);
        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    @Override
    public void flingToNext() {
        if (null != mOnViewFlipperListener && !isLastPage) {
            setInAnimation(getContext(), R.anim.left_slip_in);
            setOutAnimation(getContext(), R.anim.left_slip_out);
            mOnViewFlipperListener.getNextView();
//            int childCnt = getChildCount();
//            if (childCnt == 2) {
//                removeViewAt(1);
//            }
//            View nextView = mOnViewFlipperListener.getNextView();
//            if (indexOfChild(nextView)  != -1) {
//                removeView(nextView);
//                nextView = mOnViewFlipperListener.getNextView();
//            }
//            addView(nextView, 0);
//            setDisplayedChild(0);
//            if (0 != childCnt) {
//            setInAnimation(getContext(), R.anim.left_slip_in);
//            setOutAnimation(getContext(), R.anim.left_slip_out);

//                setDisplayedChild(0);
//            }
        }
    }

    @Override
    public void flingToPrevious() {
        if (null != mOnViewFlipperListener && !isFirstPage) {
            setInAnimation(getContext(), R.anim.right_slip_in);
            setOutAnimation(getContext(), R.anim.right_slip_out);
            mOnViewFlipperListener.getPreviousView();
//            int childCnt = getChildCount();
//            if (childCnt == 2) {
//                removeViewAt(1);
//            }
//            View previousView = mOnViewFlipperListener.getPreviousView();
//            if (indexOfChild(previousView) != - 1) {
//                removeView(previousView);
//                previousView = mOnViewFlipperListener.getPreviousView();
//            }
//            addView(previousView, 0);
//            setDisplayedChild(0);
//                setInAnimation(getContext(), R.anim.right_slip_in);
//            if (0 != childCnt) {
//                setOutAnimation(getContext(), R.anim.right_slip_out);
//                setDisplayedChild(0);
//            }
        }
    }

    public interface OnViewFlipperListener {
        View getNextView();

        View getPreviousView();
    }
}
