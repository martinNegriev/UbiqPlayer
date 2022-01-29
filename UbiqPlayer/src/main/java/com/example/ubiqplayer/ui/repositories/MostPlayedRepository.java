package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.mediastoreops.MediaStoreUtil;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.List;

public class MostPlayedRepository {

    private static MostPlayedRepository instance;
    private MutableLiveData<List<Song>> mostPlayedData = new MutableLiveData<>();

    public static MostPlayedRepository getInstance() {
        if (instance == null) {
            synchronized (MostPlayedRepository.class) {
                if (instance == null) {
                    instance = new MostPlayedRepository();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void loadMostPlayedSongs() {
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> mostPlayedData.postValue(MediaStoreUtil.queryMostPlayedSongs()));
    }

    public MutableLiveData<List<Song>> getMostPlayedData() {
        return mostPlayedData;
    }
}
