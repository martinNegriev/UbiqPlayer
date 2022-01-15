package com.example.ubiqplayer.mediaplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
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
        LocalBroadcastManager.getInstance(act).registerReceiver(receiver, intentFilter)
        registered = true
    }

    fun tryUnregisterReceiver() {
        LocalBroadcastManager.getInstance(act).unregisterReceiver(receiver)
        registered = false
    }

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when {
                action == MediaPlayerActions.ACTION_REFRESH_UI -> {
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
                    if (frag is BaseFragment)
                        frag.notifyAdapterDataSetChanged()
                    act.musicBottomSheet.refreshUI()
                }

                action == MediaPlayerActions.ACTION_HIDE_UI -> {
                    act.fab.visibility = View.GONE
                    act.musicBottomSheet.hideBottomSheet()
                }
                else -> assert(false)
            }
        }

    }

}