package com.iiitd.hammad13060.trackme;

import android.app.Application;
import android.content.Context;

/**
 * Created by hammad on 18/1/16.
 */
public class GenericApplication extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
