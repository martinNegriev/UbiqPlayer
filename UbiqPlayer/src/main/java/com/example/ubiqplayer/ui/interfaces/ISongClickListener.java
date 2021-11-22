package com.example.ubiqplayer.ui.interfaces;

import androidx.annotation.NonNull;

import com.example.ubiqplayer.ui.models.Song;

public interface ISongClickListener {

    void onClick(@NonNull Song playedSong);
}
