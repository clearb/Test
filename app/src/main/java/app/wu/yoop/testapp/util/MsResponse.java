package app.wu.yoop.testapp.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Silva on 2017/8/16.
 */

public class MsResponse {
    private static final String TAG = "MsResponse";

    public static final int MS_SUCCESS = 1;
    public static final int ERROR_USERNAME_OR_PASSWD = 201; //用户名或密码错误
    public static final int ERROR_TOKEN_INVALID = 203; //token无效
    public static final int ERROR_TOKEN_EXPIRED = 204; //token已过期

    public static final String PARAM_CODE = "code";
    public static final String PARAM_RESPONSE = "body";

    public JSONObject json;
    public int code;
    public String response;
    public Object vaslue;

    public boolean isSuccess (MsResponse mr) {
        return mr != null && mr.code == MS_SUCCESS;
    }

    public boolean isSuccess () {
        return code == MS_SUCCESS;
    }

    public static MsResponse fromJson (String src) {
        MsResponse response = new MsResponse();
        try {
            JSONObject json = new JSONObject(src);
            response.json = json;
            response.code = json.getInt(PARAM_CODE);
            response.response = json.getString(PARAM_RESPONSE);
        } catch (JSONException e) {
            Utils.logD(TAG, "Parse Msresponse Error: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject getJSONObject () {
        if (json != null) {
            return json.optJSONObject(PARAM_RESPONSE);
        } else {
            return null;
        }
    }

    public JSONArray getJSONArray () {
        return json.optJSONArray(PARAM_RESPONSE);
    }

}
