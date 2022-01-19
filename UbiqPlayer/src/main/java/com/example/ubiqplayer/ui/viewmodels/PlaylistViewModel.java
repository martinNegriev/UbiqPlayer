package com.example.ubiqplayer.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.ui.repositories.PlaylistRepository;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {

    private MutableLiveData<List<PlaylistWithSongs>> playlistsData;
    private final PlaylistRepository playlistRepository = PlaylistRepository.getInstance();

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        playlistsData = playlistRepository.getPlaylistsData();
    }

    public MutableLiveData<List<PlaylistWithSongs>> getPlaylistsData() {
        return playlistsData;
    }

    public void loadPlaylistsData() {
        playlistRepository.loadPlaylists();
    }
}
