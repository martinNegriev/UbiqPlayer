package com.example.ubiqplayer.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when {
            action == MediaPlayerActions.ACTION_OPEN_PLAYER -> {
                // open player
            }
            action == MediaPlayerActions.ACTION_NOTIFICATION_DISMISSED -> {
                // dismiss
                MediaPlayerService.killService()
            }
        }
    }
}