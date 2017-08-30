package app.wu.yoop.testapp.widget;


import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;


/**
 * Created by wuyan on 2017/7/9 0009.
 */
public class MGestureListener extends SimpleOnGestureListener {

    private OnFlingListener mOnFlingListener;

    public OnFlingListener getOnFlingListener() {
        return mOnFlingListener;
    }

    public void setOnFlingListener(OnFlingListener mOnFlingListener) {
        this.mOnFlingListener = mOnFlingListener;
    }

    @Override
    public final boolean onFling(final MotionEvent e1, final MotionEvent e2,
                                 final float speedX, final float speedY) {
        if (mOnFlingListener == null) {
            return super.onFling(e1, e2, speedX, speedY);
        }

        float XFrom = e1.getX();
        float XTo = e2.getX();
        float YFrom = e1.getY();
        float YTo = e2.getY();
        // 左右滑动的X轴幅度大于100，并且X轴方向的速度大于100
        if (Math.abs(XFrom - XTo) > 100.0f && Math.abs(speedX) > 100.0f) {
            // X轴幅度大于Y轴的幅度
            if (Math.abs(XFrom - XTo) >= Math.abs(YFrom - YTo)) {
                if (XFrom > XTo) {
                    // 下一个
                    mOnFlingListener.flingToNext();
                } else {
                    // 上一个
                    mOnFlingListener.flingToPrevious();
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public interface OnFlingListener {
        void flingToNext();

        void flingToPrevious();
    }
}