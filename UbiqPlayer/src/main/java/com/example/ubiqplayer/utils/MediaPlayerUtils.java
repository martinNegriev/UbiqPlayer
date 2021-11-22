package com.example.ubiqplayer.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;

public class MediaPlayerUtils {

    public static final String MEDIA_PLAYER_NOTIFICATION_CHANNEL = "media_player_channel";

    public static NotificationCompat.Builder createMediaPlayerNotificationBuilder() {
        return new NotificationCompat.Builder(App.get(), MEDIA_PLAYER_NOTIFICATION_CHANNEL);
    }

    public static void registerMediaPlayerChannel() {
        NotificationManager manager = (NotificationManager) App.get().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel musicChannel = new NotificationChannel(MEDIA_PLAYER_NOTIFICATION_CHANNEL, App.get().getString(R.string.ubiq_player_channel_str), NotificationManager.IMPORTANCE_LOW);
        musicChannel.enableVibration(false);
        musicChannel.setVibrationPattern(null);
        manager.createNotificationChannel(musicChannel);
    }
}
