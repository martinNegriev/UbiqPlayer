package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.ui.models.Song;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;

public class QueueRepository {

    private static QueueRepository instance;
    private final MutableLiveData<List<Song>> songsData = new MutableLiveData<>();

    public static QueueRepository getInstance() {
        if (instance == null) {
            synchronized (QueueRepository.class) {
                if (instance == null) {
                    instance = new QueueRepository();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void loadQueuedSongs() {
        if (MediaPlayerService.getState() == ExoPlayer.STATE_IDLE) {
            songsData.postValue(new ArrayList<>());
            return;
        }
        songsData.postValue(MediaPlayerService.getSongsQueue());
    }

    public MutableLiveData<List<Song>> getSongsData() {
        return songsData;
    }
}
