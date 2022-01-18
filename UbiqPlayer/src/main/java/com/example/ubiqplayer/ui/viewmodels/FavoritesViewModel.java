package com.example.ubiqplayer.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.repositories.FavoritesRepository;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {
    private MutableLiveData<List<Song>> favsData;
    private final FavoritesRepository favoritesRepository = FavoritesRepository.getInstance();

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        favsData = favoritesRepository.getFavsData();
    }

    public MutableLiveData<List<Song>> getFavsData() {
        return favsData;
    }

    public void loadFavsData() {
        favoritesRepository.loadFavSongs();
    }

}
