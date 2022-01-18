package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.mediastoreops.MediaStoreUtil;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.List;

public class FavoritesRepository {

    private static FavoritesRepository instance;
    private MutableLiveData<List<Song>> favsData = new MutableLiveData<>();

    public static FavoritesRepository getInstance() {
        if (instance == null) {
            synchronized (FavoritesRepository.class) {
                if (instance == null) {
                    instance = new FavoritesRepository();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void loadFavSongs() {
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> favsData.postValue(MediaStoreUtil.queryFavorites()));
    }

    public MutableLiveData<List<Song>> getFavsData() {
        return favsData;
    }
}
