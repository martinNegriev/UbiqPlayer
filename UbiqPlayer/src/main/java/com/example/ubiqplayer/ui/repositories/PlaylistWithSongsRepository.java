package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.persistence.SongDatabase;
import com.example.ubiqplayer.ui.interfaces.ISongRepository;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.sorting.Comparators;
import com.example.ubiqplayer.ui.sorting.SortOption;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistWithSongsRepository implements ISongRepository {

    private static PlaylistWithSongsRepository instance;
    private MutableLiveData<List<Song>> songsData = new MutableLiveData<>();

    public static PlaylistWithSongsRepository getInstance() {
        if (instance == null) {
            synchronized (PlaylistWithSongsRepository.class) {
                if (instance == null) {
                    instance = new PlaylistWithSongsRepository();
                    return instance;
                }
            }
        }
        return instance;
    }

    public MutableLiveData<List<Song>> getSongsData() {
        return songsData;
    }

    public void loadSongsForPlaylist(String playlistName, SortOption sortOption, boolean reversed) {
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> {
            PlaylistWithSongs playlistWithSongs = SongDatabase.getInstance().songDao().getPlaylistWithSongsByName(playlistName);
            if (playlistWithSongs == null || playlistWithSongs.songs == null || playlistWithSongs.songs.isEmpty()) {
                songsData.postValue(new ArrayList<>());
                return;
            }
            List<Song> playlistSongs = playlistWithSongs.songs;
            sortAndPostSongs(sortOption, playlistSongs, songsData, reversed);
        });
    }
}
