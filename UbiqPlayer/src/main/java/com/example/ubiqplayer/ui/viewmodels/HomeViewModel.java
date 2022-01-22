package com.example.ubiqplayer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.repositories.SongRepository;
import com.example.ubiqplayer.ui.sorting.SortOption;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<List<Song>> songsData;
    protected final SongRepository songRepository = SongRepository.getInstance();

    public HomeViewModel(Application application) {
        super(application);
        songsData = songRepository.getSongsData();
    }

    public LiveData<List<Song>> getSongsData() {
        return songsData;
    }

    public void loadSongsData(SortOption sortOption, boolean reversed) {
        songRepository.loadSongs(sortOption, reversed);
    }

    public void loadSongsData() {
        songRepository.loadSongs();
    }
}
