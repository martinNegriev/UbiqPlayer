package com.example.ubiqplayer.mediaplayer

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.view.View
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ubiqplayer.BaseFragment
import com.example.ubiqplayer.UbiqPlayerActivity
import com.example.ubiqplayer.UbiqPlayerNavHostFragment
import com.example.ubiqplayer.persistence.SongDatabase
import com.example.ubiqplayer.ui.helper.ResultTask
import com.example.ubiqplayer.ui.mostplayed.MostPlayedFragment

class UbiqPlayerLogic(val act: UbiqPlayerActivity) {
    private var registered = false;

    fun tryRegisterReceiver() {
        if (registered)
            return
        val intentFilter = IntentFilter()
        intentFilter.addAction(MediaPlayerActions.ACTION_HIDE_UI)
        intentFilter.addAction(MediaPlayerActions.ACTION_REFRESH_UI)
        intentFilter.addAction(MediaPlayerActions.ACTION_REFRESH_UI_ITEM_TRANSITION)
        intentFilter.addAction(MediaPlayerActions.ACTION_UPDATE_MOST_PLAYED)
        LocalBroadcastManager.getInstance(act).registerReceiver(receiver, intentFilter)
        registered = true
    }

    fun tryUnregisterReceiver() {
        LocalBroadcastManager.getInstance(act).unregisterReceiver(receiver)
        registered = false
    }

    fun getCurrentFragment(): Fragment? {
        var frag = act.supportFragmentManager.fragments.last()
        if (frag is UbiqPlayerNavHostFragment) {
            if (frag.isAttached())
                frag = frag.childFragmentManager.fragments.last()
            else
                frag = frag.childFragmentManager.fragments.last()
        }
        return frag
    }

    @SuppressLint("StaticFieldLeak")
    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val songUri = intent.getParcelableExtra<Uri>(MediaPlayerActions.EXTRA_SONG_URI)
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
                    MediaPlayerService.setCurrentSong(null)
                    MediaPlayerService.setSongsQueue(null)
                    val frag = getCurrentFragment()
                    if (frag is BaseFragment) {
                        frag.notifyAdapterDataSetChanged()
                        frag.reloadIfNeeded()
                    }
                }

                action == MediaPlayerActions.ACTION_UPDATE_MOST_PLAYED -> {
                    object : ResultTask<Int>(){
                        override fun doInBackground(): Int {
                            return  SongDatabase.getInstance().songDao().updateTimesPlayed(songUri)
                        }

                        override fun onPostExecute(result: Int) {
                            if (result < 1)
                                return
                            val frag = getCurrentFragment()
                            if (frag is MostPlayedFragment) {
                                frag.notifyAdapterDataSetChanged()
                                frag.reloadIfNeeded()
                            }
                        }
                    }.start()
                }
                else -> assert(false)
            }
        }

    }

}