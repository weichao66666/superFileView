package com.silang.superfileview.application;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QbSdk.initX5Environment(this, null);
        ExceptionHandler.getInstance().initConfig(this);
    }
}