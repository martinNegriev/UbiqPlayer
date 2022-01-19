package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.ui.mediastoreops.MediaStoreUtil;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.List;

public class PlaylistRepository {

    private static PlaylistRepository instance;
    private MutableLiveData<List<PlaylistWithSongs>> playlistsData = new MutableLiveData<>();

    public static PlaylistRepository getInstance() {
        if (instance == null) {
            synchronized (PlaylistRepository.class) {
                if (instance == null) {
                    instance = new PlaylistRepository();
                    return instance;
                }
            }
        }
        return instance;
    }

    public void loadPlaylists() {
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> playlistsData.postValue(MediaStoreUtil.queryPlaylists()));
    }

    public MutableLiveData<List<PlaylistWithSongs>> getPlaylistsData() {
        return playlistsData;
    }
}
