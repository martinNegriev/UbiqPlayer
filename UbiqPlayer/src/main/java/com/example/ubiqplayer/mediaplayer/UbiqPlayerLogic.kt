package com.example.ubiqplayer.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ubiqplayer.UbiqPlayerActivity

class UbiqPlayerLogic(val act: UbiqPlayerActivity) {
    private var registered = false;

    fun tryRegisterReceiver() {
        if (registered)
            return
        val intentFilter = IntentFilter()
        intentFilter.addAction(MediaPlayerActions.ACTION_HIDE_UI)
        intentFilter.addAction(MediaPlayerActions.ACTION_REFRESH_UI)
        LocalBroadcastManager.getInstance(act).registerReceiver(receiver, intentFilter)
        registered = true
    }

    fun tryUnregisterReceiver() {
        registered = false
    }

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when {
                action == MediaPlayerActions.ACTION_REFRESH_UI -> act.musicBottomSheet.refreshUI()

                action == MediaPlayerActions.ACTION_HIDE_UI -> {
                    act.fab.visibility = View.GONE
                    act.musicBottomSheet.hideBottomSheet()
                }
                else -> assert(false)
            }
        }

    }

}