package com.example.ubiqplayer.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.repositories.MostPlayedRepository;

import java.util.List;

public class MostPlayedViewModel extends AndroidViewModel {

    private MutableLiveData<List<Song>> mostPlayedData;
    private final MostPlayedRepository mostPlayedRepository = MostPlayedRepository.getInstance();

    public MostPlayedViewModel(@NonNull Application application) {
        super(application);
        mostPlayedData = mostPlayedRepository.getMostPlayedData();
    }

    public MutableLiveData<List<Song>> getMostPlayedData() {
        return mostPlayedData;
    }

    public void loadMostPlayedData() {
        mostPlayedRepository.loadMostPlayedSongs();
    }
}
