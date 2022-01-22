package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.interfaces.ISongRepository;
import com.example.ubiqplayer.ui.mediastoreops.MediaStoreUtil;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.sorting.SortOption;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.List;

public class SongRepository implements ISongRepository {

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

    public void loadSongs(SortOption sortOption, boolean reversed) {
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> {
            List<Song> songs = MediaStoreUtil.querySongs();
            sortAndPostSongs(sortOption, songs, songsData, reversed);
        });
    }

    public MutableLiveData<List<Song>> getSongsData() {
        return songsData;
    }
}
