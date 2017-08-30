package app.wu.yoop.testapp.util;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import app.wu.yoop.testapp.TestApp;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Silva on 2017/8/11.
 */

public class HttpUtil {

    public static final String TAG = "HttpResult";

    public static Response get(String url, Object tag, Map<String, String> parmas) {
        if (checkNetWork())
            return null;
        try {
            return OkHttpUtils.get().url(url).params(getParmas(parmas)).tag(tag).build().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response get(String url, Map<String, String> params) {
        return get(url, null, params);
    }

    public static void get(final String url, Object tag, final Map<String,String> parmas, final Callback callBack) {
        if (checkNetWork()) {
            if (callBack != null) {
                callBack.onError(null, new NetworkErrorException("Network disconnection"), -1);
            }
            return;
        }
        if (callBack instanceof StringCallback) {
            OkHttpUtils.get().url(url).params(getParmas(parmas)).tag(tag).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Utils.logD(TAG,"Request server failed, " + getRequestUrl(url, getParmas(parmas))+ ":" + e.getMessage());
                    callBack.onError(call, e, id);
                }

                @Override
                public void onResponse(String response, int id) {
                    Utils.logD(TAG, getRequestUrl(url, getParmas(parmas)) + ":" + response);
                    callBack.onResponse(response, id);
                }
            });
        } else {
            OkHttpUtils.get().url(url).params(getParmas(parmas)).tag(tag).build().execute(callBack);
        }
    }

    public static void  get(String url, Map<String, String> parmas, Callback callBack) {
        get(url, null, parmas, callBack);
    }

    public static Response post(String url, Object tag, Map<String, String> parmas) {
        if (checkNetWork())
            return null;
        try {
            return OkHttpUtils.post().url(url).params(getParmas(parmas)).tag(tag).build().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response post(String url, Map<String, String> parmas) {
        return post(url, null, parmas);
    }

    public static void post(String url, Map<String, String> params, final Callback callback) {
        post(url, null, params, callback);
    }

    public static void post(final String url, Object tag, final Map<String, String> parmas, final Callback callBack) {
        if (checkNetWork()) {
            if (callBack != null) {
                callBack.onError(null, new NetworkErrorException("NetWork disconnection"), -1);
            }
            return;
        }
        if (callBack instanceof  StringCallback) {
            OkHttpUtils.post().url(url).params(getParmas(parmas)).tag(tag).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Utils.logD(TAG, "Request server failed, " + getRequestUrl(url, getParmas(parmas)) + ":" +e.getMessage());
                    callBack.onError(call, e, id);
                }

                @Override
                public void onResponse(String response, int id) {
                    Utils.logD(TAG, getRequestUrl(url, getParmas(parmas)) + ":" + response);
                    callBack.onResponse(response, id);
                }
            });
        } else {
            OkHttpUtils.post().url(url).params(getParmas(parmas)).tag(tag).build().execute(callBack);
        }
    }

    public static Response postJosn(String url,String json) {
        if (checkNetWork())
            return null;
        try {
            return OkHttpUtils.postString().url(url).content(json).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void postJson(String url, String json, Callback callBack) {
        if (checkNetWork()) {
            if  (callBack != null) {
                callBack.onError(null, new NetworkErrorException("NetWork disconnection"), -1);
            }
            return;
        }
        OkHttpUtils.postString().url(url).content(json).mediaType(MediaType.parse("application/json; charset=utf-8")).build().execute(callBack);
    }

    public static void postFile (String file, Callback callBack) {
        if (checkNetWork()) {
            if  (callBack != null) {
                callBack.onError(null, new NetworkErrorException("NetWork disconnection"), -1);
            }
            return;
        }
        OkHttpUtils.postFile().file(new File(file)).build().execute(callBack);
    }

    public static void downLoad (String url, Callback callBack) {
        if (checkNetWork()) {
            if (callBack != null) {
                callBack.onError(null, new NetworkErrorException("Network disconnection"), -1);
            }
            return;
        }
        OkHttpUtils.get().url(url).build().execute(callBack);
    }

    private static String getRequestUrl(String url, Map<String, String> parmas) {
        String requestUrl = url + "?";
        boolean isFrist = true;
        for (String key : parmas.keySet()) {
            if (isFrist) {
                isFrist = false;
            } else {
                requestUrl += "&";
            }
            requestUrl += key;
            requestUrl += "=";
            requestUrl += parmas.get(key);
        }
        return requestUrl;
    }


    private static boolean checkNetWork() {
        if (!isNetWorkAvailable(TestApp.getAppContext())) {
            Toast.makeText(TestApp.getAppContext(), "网络不给力，请检测网络设置!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        boolean isAvailable = ni != null && ni.isConnected();
        Log.d(TAG, "isNetWorkAvailable: " + isAvailable);
        return isAvailable;
    }

    private static Map<String, String> getParmas(Map<String, String> params) {
        if (params == null) {
            params = new ArrayMap<>();
        }
        params.put("client_os", "android");
//        if (sAccountInfoHelper != null && sAccountInfoHelper.isLoggedIn()) {
            params.put("uid", String.valueOf(5));
            params.put("token", "5bc9f4d6-40d6-4c6a-a43f-bc4e1004bd5f");
//        }
        return params;
    }
}
