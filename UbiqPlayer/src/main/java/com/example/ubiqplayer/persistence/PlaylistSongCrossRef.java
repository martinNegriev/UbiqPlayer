package com.example.ubiqplayer.persistence;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(primaryKeys = {"playlistId", "songUri"})
public class PlaylistSongCrossRef {

    public long playlistId;
    @NonNull public Uri songUri;
}
