package com.example.ubiqplayer.ui.interfaces;

import androidx.annotation.NonNull;

import com.example.ubiqplayer.persistence.PlaylistWithSongs;

public interface IPlaylistClickListener {

    void onClick(@NonNull PlaylistWithSongs clicked);

}
