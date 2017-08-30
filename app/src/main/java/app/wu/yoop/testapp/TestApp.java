package app.wu.yoop.testapp;

import android.app.Application;
import android.content.Context;

import app.wu.yoop.testapp.util.Utils;

/**
 * Created by Silva on 2017/8/11.
 */

public class TestApp extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Utils.init(this);
    }

    public static Context getAppContext() {
        return mContext;
    }
}
