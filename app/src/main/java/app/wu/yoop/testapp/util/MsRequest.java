package app.wu.yoop.testapp.util;

/**
 * Created by Silva on 2017/8/16.
 */

public final class MsRequest {

    public static final int GET = 1;
    public static final int POST = 2;
    public static final int MULTI_PART_POST = 2;

    private String mApi;
    private String mName;
    private int mType;
    private boolean mRequireAuth;

    private String mUrl;
    private String mDesc;

    public MsRequest (String api,String name, int type,boolean requireAuth) {
        mApi = api;
        mName = name;
        mType = type;
        mRequireAuth = requireAuth;
    }

    public String getUrl () {
        if (mUrl == null) {
            mUrl = Utils.getServerAddress() + mApi + "/" + getName();
        }
        return mUrl;
    }

    public String getDesc () {
        if (mDesc == null) {
            mDesc = mApi + ":" + mName;
        }
        return mDesc;
    }

    public String getApi() {
        return mApi;
    }

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public boolean requireAuth() {
        return mRequireAuth;
    }

    private static final String APP = "AppApi";
    public static final MsRequest APP_GET_BANNERS  = new MsRequest(APP, "listBanner", GET, false);
}
