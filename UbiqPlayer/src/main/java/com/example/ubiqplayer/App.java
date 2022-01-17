package com.example.ubiqplayer;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.example.ubiqplayer.utils.MediaPlayerUtils;

public class App extends Application {

    private static Context context;
    public static final Handler HANDLER = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        MediaPlayerUtils.registerMediaPlayerChannel();
    }

    public static Context get() {
        return context;
    }
}
