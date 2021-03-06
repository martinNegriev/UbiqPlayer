package com.example.ubiqplayer.mediaplayer

class MediaPlayerActions {

    companion object {

        // Notification actions
        @JvmField
        val ACTION_OPEN_PLAYER = "OPEN_PLAYER"

        @JvmField
        val ACTION_NOTIFICATION_DISMISSED = "NOTIFICATION_DISMISSED"

        @JvmField
        val ACTION_PLAY_PAUSE = "PLAY_PAUSE"

        @JvmField
        val ACTION_NEXT = "NEXT"

        @JvmField
        val ACTION_PREV = "PREV"

        // Player view actions
        @JvmField
        val ACTION_HIDE_UI = "HIDE_UI"

        @JvmField
        val ACTION_REFRESH_UI = "REFRESH_UI"

        @JvmField
        val ACTION_REFRESH_UI_ITEM_TRANSITION = "REFRESH_UI_ITEM_TRANSITION"

        @JvmField
        val ACTION_UPDATE_MOST_PLAYED = "UPDATE_MOST_PLAYED"

        @JvmField
        val EXTRA_SONG_URI = "EXTRA_SONG_URI"
    }
}