package com.example.ubiqplayer.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Playlist {

    @PrimaryKey
    public long playlistId;

    @ColumnInfo(name = "playlistName")
    public String playlistName;
}
