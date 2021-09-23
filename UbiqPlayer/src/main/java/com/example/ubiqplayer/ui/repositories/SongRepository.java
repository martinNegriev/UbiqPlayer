package com.example.ubiqplayer.ui.repositories;

import androidx.lifecycle.MutableLiveData;

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
        List<Song> songs = new ArrayList<>();
        new Thread( () -> {
            // TODO Query MediaStore
            songs.clear();
            for (int i = 0; i < 20; i++) {
                songs.add(new Song(null, 0, null, null));
            }
            songsData.postValue(songs);
        }).start();
    }

    public MutableLiveData<List<Song>> getSongsData() {
        return songsData;
    }
}
