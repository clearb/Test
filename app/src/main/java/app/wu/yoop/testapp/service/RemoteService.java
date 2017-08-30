package app.wu.yoop.testapp.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import app.wu.yoop.testapp.aidl.ServiceConnector;

/**
 * Created by wuyan on 2016/12/15 0015.
 */

public class RemoteService extends Service {

    private static final String TAG = "YPService";
    private RemoteConnector mBinder;
    private RemoteConnection mConnection;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new RemoteConnector();
        mConnection = new RemoteConnection();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand: RemoteService");
        bindService(new Intent(this, LocalService.class), mConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startAndBindService();
    }

    private class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
            Log.d(TAG, "RemoteService onServiceConnected: " + componentName.getShortClassName());
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            Log.d(TAG, "RemoteService onServiceDisconnected:" + componentName.getShortClassName());
            startAndBindService();
        }
    }

    private void startAndBindService() {
        RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));
        RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class), mConnection, Context.BIND_IMPORTANT);
    }

    private class RemoteConnector extends ServiceConnector.Stub {

        @Override
        public String getComponentName() throws RemoteException {
            return "RemoteService";
        }
    }
}
