package app.wu.yoop.testapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import app.wu.yoop.testapp.R;

/**
 * Created by wuyan on 2017/6/28 0028.
 */

public class ExpandableTextView extends TextView {

    private int mMaxLine;

    private Context mContext;

    private boolean shouldChangText = true;

    private String mOrignalText;

    public ExpandableTextView(final Context context) {
        this(context, null);
    }

    public ExpandableTextView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context.getApplicationContext();
    }

    public void setMaxLineShown(int maxLine) {
        this.mMaxLine = maxLine;
    }

    public void setShouldChangeText() {
        shouldChangText = true;
    }

    @Override
    public void setText(final CharSequence text, final BufferType type) {
        super.setText(text, type);
        if (TextUtils.isEmpty(text) || !shouldChangText) {
            return;
        }
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!shouldChangText) {
                    return;
                }
                mOrignalText = getText().toString();
                shouldChangText = false;
                Layout layout = getLayout();
                int lineCount = layout.getLineCount();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < lineCount; i++) {
                    if (i < mMaxLine) {
                        int lineStart = layout.getLineStart(i);
                        int lineEnd = layout.getLineEnd(i);
                        CharSequence sub = getText().subSequence(lineStart, lineEnd);
                        sb.append(sub);
                    } else {
                        break;
                    }
                }
                String result = sb.toString();
                if (!TextUtils.isEmpty(result) && result.length() > 2) {
                    result = result.replace(result.subSequence(result.length() - 2, result.length() - 1), "...");
                    SpannableString ss = new SpannableString(result);
                    ss.setSpan(getLoadMoreSpan(), result.length() - 1, result.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setText(ss);
                }
            }
        });
    }

    private ImageSpan getLoadMoreSpan() {
        return new CenteredImageSpan(mContext, R.drawable.open);
    }


    public class CenteredImageSpan extends ImageSpan {

        public CenteredImageSpan(Context context, final int drawableRes) {
            super(context, drawableRes);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    setText(mOrignalText);
                }
            });
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text,
                         int start, int end, float x,
                         int top, int y, int bottom, @NonNull Paint paint) {
            // image to draw
            Drawable b = getDrawable();
            // font metrics of text to be replaced
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            int transY = (y + fm.descent + y + fm.ascent) / 2
                    - b.getBounds().bottom / 2;

            canvas.save();
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }

}
