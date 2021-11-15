package com.example.ubiqplayer.mediaplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MediaPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        const val MEDIA_PLAYER_ACTIVITY_ACTION = "MEDIA_PLAYER_ACTIVITY_ACTION"
    }
}