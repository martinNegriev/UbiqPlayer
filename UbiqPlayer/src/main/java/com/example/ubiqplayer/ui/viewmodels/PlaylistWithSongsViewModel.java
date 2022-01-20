package com.example.ubiqplayer.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.repositories.PlaylistWithSongsRepository;

import java.util.List;

public class PlaylistWithSongsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Song>> songsData;
    private final PlaylistWithSongsRepository songRepository = PlaylistWithSongsRepository.getInstance();

    public PlaylistWithSongsViewModel(@NonNull Application application) {
        super(application);
        songsData = songRepository.getSongsData();
    }

    public LiveData<List<Song>> getSongsData() {
        return songsData;
    }

    public void loadSongsForPlaylist(String playlistName) {
        songRepository.loadSongsForPlaylist(playlistName);
    }
}
