package com.map4d.service;

import android.app.Application;

public class WifiCheck extends Application {
    static WifiCheck wifiInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        wifiInstance = this;
    }
    public static synchronized WifiCheck getInstance() {
        return wifiInstance;
    }
}
