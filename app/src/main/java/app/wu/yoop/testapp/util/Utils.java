package app.wu.yoop.testapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import app.wu.yoop.testapp.R;

/**
 * Created by YoopWu on 2017/6/27 0027.
 */

public class Utils {

    private static String sServerAddress;
    private static final String SERVER_PROTOCOL = "http://";
    private static String sServerDomain;
    private static boolean sDebug;
    private static boolean sMtaEnabled;
    private static boolean sUseDevServer;

    private static final String TAG = Utils.class.getSimpleName();

    public static void logE(String tag, String message) {
        Log.e(tag, message);
    }

    public static boolean unzipFile(String zipPath, String destPath) {
        ZipFile zipFile = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        byte[] buffer = new byte[4096];
        try {
            zipFile = new ZipFile(zipPath);
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = e.nextElement();
                File file = new File(destPath, entry.getName());
                if (entry.isDirectory()) {
                    if (!file.exists() && !file.mkdirs()) {
                        return false;
                    }
                } else {
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists() && !parentFile.mkdirs()) {
                        return false;
                    }
                    bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    int readLen = 0;
                    while ((readLen = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, readLen);
                    }
                    bis.close();
                    bos.close();
                }
            }
        } catch (IOException e) {
            Utils.logE(TAG, e.getMessage());
            return false;
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
        }

        return true;
    }

    public static void logD(String tag, String msg) {
        Log.d(tag, msg == null ? "" : msg);
    }

    public static void init(Context context) {
        Resources res = context.getResources();
        sDebug = res.getBoolean(R.bool.debug);
        sUseDevServer = res.getBoolean(R.bool.use_dev_server);

        if (sUseDevServer) {
            sMtaEnabled = false;
            sServerDomain = res.getString(R.string.server_domain_dev);
        } else {
            sMtaEnabled = !sDebug;
            sServerDomain = res.getString(R.string.server_domain_prod);
        }
    }

    public static String getServerAddress() {
        if (sServerAddress == null) {
            sServerAddress = SERVER_PROTOCOL + sServerDomain;
            sServerAddress += "/";
        }
        logD(TAG, "server address : " + sServerAddress);
        return sServerAddress;
    }
}
