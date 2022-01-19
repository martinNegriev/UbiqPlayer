package com.example.ubiqplayer.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ubiqplayer.BaseFragment
import com.example.ubiqplayer.UbiqPlayerActivity
import com.example.ubiqplayer.UbiqPlayerNavHostFragment

class UbiqPlayerLogic(val act: UbiqPlayerActivity) {
    private var registered = false;

    fun tryRegisterReceiver() {
        if (registered)
            return
        val intentFilter = IntentFilter()
        intentFilter.addAction(MediaPlayerActions.ACTION_HIDE_UI)
        intentFilter.addAction(MediaPlayerActions.ACTION_REFRESH_UI)
        intentFilter.addAction(MediaPlayerActions.ACTION_REFRESH_UI_ITEM_TRANSITION)
        LocalBroadcastManager.getInstance(act).registerReceiver(receiver, intentFilter)
        registered = true
    }

    fun tryUnregisterReceiver() {
        LocalBroadcastManager.getInstance(act).unregisterReceiver(receiver)
        registered = false
    }

    fun getCurrentFragment(): Fragment? {
        var frag = act.supportFragmentManager.fragments.last()
        var navFrag = act.supportFragmentManager.primaryNavigationFragment
        if (frag.equals(navFrag) && frag is UbiqPlayerNavHostFragment) {
            if (frag.isAttached())
                frag = frag.childFragmentManager.fragments.last()
            else {
                navFrag = act.supportFragmentManager.primaryNavigationFragment
                frag = navFrag?.childFragmentManager?.fragments?.last()
            }
        }
        return frag
    }

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when {
                action == MediaPlayerActions.ACTION_REFRESH_UI || action == MediaPlayerActions.ACTION_REFRESH_UI_ITEM_TRANSITION -> {
                    val frag = getCurrentFragment()
                    if (frag is BaseFragment)
                        frag.notifyAdapterDataSetChanged()
                    act.musicBottomSheet.refreshUI()
                    if (action == MediaPlayerActions.ACTION_REFRESH_UI_ITEM_TRANSITION) {
                        act.musicBottomSheet.hideLyricsView()
                        MediaPlayerService.setCurrentSongLyricsList(null)
                    }
                }

                action == MediaPlayerActions.ACTION_HIDE_UI -> {
                    act.fab.visibility = View.GONE
                    act.musicBottomSheet.hideBottomSheet()
                    act.musicBottomSheet.hideLyricsView()
                    MediaPlayerService.setCurrentSongLyricsList(null)
                }
                else -> assert(false)
            }
        }

    }

}