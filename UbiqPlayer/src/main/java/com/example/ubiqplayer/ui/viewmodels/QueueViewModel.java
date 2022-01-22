package com.example.ubiqplayer.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.repositories.QueueRepository;

import java.util.List;

public class QueueViewModel extends AndroidViewModel {

    private MutableLiveData<List<Song>> songsData;
    protected final QueueRepository songRepository = QueueRepository.getInstance();

    public QueueViewModel(Application application) {
        super(application);
        songsData = songRepository.getSongsData();
    }

    public LiveData<List<Song>> getSongsData() {
        return songsData;
    }

    public void loadQueuedSongs() {
        songRepository.loadQueuedSongs();
    }
}
