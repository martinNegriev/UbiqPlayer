package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.mediastoreops.MediaStoreUtil;
import com.example.ubiqplayer.ui.models.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongRepository {

    private static SongRepository instance;
    private MutableLiveData<List<Song>> songsData = new MutableLiveData<>();

    public static SongRepository getInstance() {
        if (instance == null) {
            synchronized (SongRepository.class) {
                if (instance == null) {
                    instance = new SongRepository();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void loadSongs() {
        new Thread(() -> songsData.postValue(MediaStoreUtil.querySongs())).start();
    }

    public MutableLiveData<List<Song>> getSongsData() {
        return songsData;
    }
}
