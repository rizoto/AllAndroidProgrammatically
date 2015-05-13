package com.gpt.dynlayout;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by luborkolacny on 13/05/15.
 */

public class LeakMonitorApp extends Application {

    public static RefWatcher getRefWatcher(Context context) {
        LeakMonitorApp application = (LeakMonitorApp) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
        Log.d("Leak", "monitor running");
    }
}