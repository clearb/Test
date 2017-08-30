package app.wu.yoop.testapp.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by wuyan on 2016/12/27 0027.
 */

public class ThreadIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ThreadIntentService(final String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {

    }
}
