package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.mediastoreops.MediaStoreUtil;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.utils.CommonUtils;

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
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> songsData.postValue(MediaStoreUtil.querySongs()));
    }

    public MutableLiveData<List<Song>> getSongsData() {
        return songsData;
    }
}
