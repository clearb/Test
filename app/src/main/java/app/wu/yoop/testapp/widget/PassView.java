package app.wu.yoop.testapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;

import java.lang.reflect.Field;

/**
 * Created by wuyan on 2016/11/19 0019.
 */

public class PassView extends EditText {

    private Paint mPaint;
    private Paint mPaintArc;
    private Paint mPaintContent;
    private int mMaxLength;
    private int mPadding = 1;
    private int mRadius = 1;
    private int mCircleRadius;
    private int mTextLength;
    private boolean mIsAddText;
    private float mInterpolatedTime = 1;
    private PaintLastAnimation mPaintAnimation;
    private IPassWordCallBack mIPassWordCallBack;

    public PassView(Context context) {
        this(context, null);
    }

    public PassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void setIPassWordCallBack(IPassWordCallBack callback) {
        this.mIPassWordCallBack = callback;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);

        mPaintArc = new Paint();
        mPaintArc.setAntiAlias(true);
        mPaintArc.setStyle(Paint.Style.FILL);

        mPaintContent = new Paint();
        mPaintContent.setAntiAlias(true);
        mPaintContent.setColor(Color.WHITE);
        mPaintContent.setStyle(Paint.Style.FILL);

        mMaxLength = getMaxLength();
        mRadius = dp2px(6);
        mCircleRadius = dp2px(6);

        mPaintAnimation = new PaintLastAnimation();
        mPaintAnimation.setDuration(200);
        mPaintAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mTextLength == mMaxLength && mIPassWordCallBack != null) {
                    mIPassWordCallBack.completeText(getText().toString());
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private int dp2px(int i) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (i * scale + 0.5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制白色背景
        RectF rectF = new RectF(mPadding, mPadding, getMeasuredWidth() - mPadding, getMeasuredHeight() - mPadding);
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaintContent);
        // 绘制外面的线框
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint);

        float cx;
        float cy = getMeasuredHeight() / 2;
        float half = getMeasuredWidth() / mMaxLength / 2;
        mPaint.setStrokeWidth(0.5f);
        // 画线框（分割线）
        for (int i = 1; i < mMaxLength; i++) {
            cx = getMeasuredWidth() / mMaxLength * i;
            canvas.drawLine(cx, 0, cx, getMeasuredHeight(), mPaint);
        }
        // 画小圆点
        for (int i = 0; i < mMaxLength; i++) {
            cx = getMeasuredWidth() / mMaxLength * i + half;
            // 增加文字
            if (mIsAddText) {
                if (i < mTextLength - 1) {
                    canvas.drawCircle(cx, cy, mCircleRadius, mPaintArc);
                } else if (i == mTextLength - 1) {
                    canvas.drawCircle(cx, cy, mCircleRadius * mInterpolatedTime, mPaintArc);
                }
            } else {
                if (i < mTextLength - 1) {
                    canvas.drawCircle(cx, cy, mCircleRadius, mPaintArc);
                } else if (i == mTextLength - 1) {
                    canvas.drawCircle(cx, cy, mCircleRadius * (1 - mInterpolatedTime), mPaintArc);
                }
            }
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        mIsAddText = getText().toString().length() > mTextLength;
        if (mTextLength <= mMaxLength) {
            if (mPaintAnimation != null) {
                clearAnimation();
                startAnimation(mPaintAnimation);
            } else {
                invalidate();
            }
        }
        mTextLength = text.length();
    }

    private int getMaxLength() {
        int length = 0;
        try {
            InputFilter[] inputFilters = getFilters();
            for (InputFilter filter : inputFilters) {
                Class<?> c = filter.getClass();
                if (c.getName().equals("android.text.InputFilter$LengthFilter")) {
                    Field[] f = c.getDeclaredFields();
                    for (Field field : f) {
                        if (field.getName().equals("mMax")) {
                            field.setAccessible(true);
                            length = (Integer) field.get(filter);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    private class PaintLastAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mInterpolatedTime = interpolatedTime;
            postInvalidate();
        }
    }

    public interface IPassWordCallBack{
        void completeText(String content);
    }
}
