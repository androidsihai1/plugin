package com.yy.plugin;

import android.app.Application;

/**
 * Created by andy on 2020/12/13.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LoadUtils.loadClass(this);
         //AmsUtils.hookIntent();
         //AmsUtils.hookHandle();
    }
}
