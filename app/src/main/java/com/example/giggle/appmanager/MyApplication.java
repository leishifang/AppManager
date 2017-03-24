package com.example.giggle.appmanager;

import android.app.Application;
import android.content.Context;

/**
 * Created by leishifang on 2017/3/24 15:17.
 */

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
