package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.ui.mediastoreops.MediaStoreUtil;
import com.example.ubiqplayer.ui.sorting.SortOption;
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

    public void loadPlaylists(SortOption option, boolean reversed) {
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> playlistsData.postValue(MediaStoreUtil.queryPlaylists(option, reversed)));
    }

    public MutableLiveData<List<PlaylistWithSongs>> getPlaylistsData() {
        return playlistsData;
    }
}
