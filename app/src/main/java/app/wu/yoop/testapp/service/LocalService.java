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

public class LocalService extends Service {

    private static final String TAG = "YPService";

    private LocalConnector mBinder;
    private LocalConnection mConnection;

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new LocalConnector();
        mConnection = new LocalConnection();
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStartCommand: LocalService");
        bindService(new Intent(this, RemoteService.class), mConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startAndBindService();
    }

    private void startAndBindService() {
        LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
        LocalService.this.startService(new Intent(LocalService.this, RemoteService.class));
        LocalService.this.bindService(new Intent(LocalService.this, RemoteService.class), mConnection, Context.BIND_IMPORTANT);
    }

    private class LocalConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
            Log.d(TAG, "LocalService RemoteService onServiceConnected: " + componentName.getShortClassName());
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            Log.d(TAG, "LocalService onServiceDisconnected: " + componentName.getShortClassName());
            startAndBindService();
        }
    }

    private class LocalConnector extends ServiceConnector.Stub {

        @Override
        public String getComponentName() throws RemoteException {
            return "LocalService";
        }
    }

}
