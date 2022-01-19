package com.example.ubiqplayer.persistence;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"playlistName", "songUri"})
public class PlaylistSongCrossRef {

    @NonNull
    public String playlistName;
    @NonNull
    public Uri songUri;
}
