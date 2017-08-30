package app.wu.yoop.testapp.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by wuyan on 2017/4/25 0025.
 */

public class AutoScaleImageView extends ImageView {

    public AutoScaleImageView(final Context context) {
        super(context);
    }

    public AutoScaleImageView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScaleImageView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AutoScaleImageView setImageUrl(String url) {

        return this;
    }
}
