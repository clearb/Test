package app.wu.yoop.testapp.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import app.wu.yoop.testapp.R;

/**
 * Created by wuyan on 2017/7/10 0010.
 */

public class WrapWebView extends LinearLayout implements Html5WebView.WebViewListener {

    public static final int COLOR_DEFAULT = 0xffebf0f2;
    public static final int COLOR_PARCHMENT = 0xffe3ddcb;
    public static final int COLOR_CARE_EYE_GREEN = 0xffcce5d4;
    public static final int COLOR_NIGHT = 0xff262626;
    public static final int BG_COLOR_NIGHT = 0xff444545;
    public static final int BG_COLOR_NORMAL = 0xffffffff;


    Html5WebView mWebView;
    private int currentSettingsColor = COLOR_DEFAULT;

    public WrapWebView(final Context context) {
        this(context, null);
    }

    public WrapWebView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapWebView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.item_web_view, this);
        mWebView = (Html5WebView) findViewById(R.id.webView);
        mWebView.init();
        mWebView.setWebViewListener(this);

        disableTextSelected();

        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(false);

    }

    private void disableTextSelected() {
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        mWebView.setLongClickable(false);
        mWebView.setHapticFeedbackEnabled(false);
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public void onWebBgChanged() {
        if (currentSettingsColor == COLOR_NIGHT) {
             currentSettingsColor = COLOR_DEFAULT;
        } else {
            currentSettingsColor = COLOR_NIGHT;
        }
        changePageBackgroundColor();
    }

    @Override
    public void onPageStarted(final String url) {

    }

    @Override
    public void onPageFinished(final String url) {
        changePageBackgroundColor();
    }

    private void changePageBackgroundColor() {

        String cssName = "normal";
        if (currentSettingsColor == COLOR_NIGHT) {
            cssName = "night";
        } else if (currentSettingsColor == COLOR_CARE_EYE_GREEN) {
            cssName = "eye_care_green";
        } else if (currentSettingsColor == COLOR_PARCHMENT) {
            cssName = "parchment";
        }

        String cssPath = "file:///android_asset/" + cssName + ".css";

        mWebView.loadUrl(getOutCss(cssPath));
    }

    public static String getOutCss(String url) {

        String js = "javascript:var d=document;" +
                "var s=d.createElement('link');" +
                "s.setAttribute('rel', 'stylesheet');" +
                "s.setAttribute('href', '" + url + "');" +
                "d.head.appendChild(s);";
        return js;
    }
}
