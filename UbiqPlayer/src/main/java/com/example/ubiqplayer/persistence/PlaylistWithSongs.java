package com.example.ubiqplayer.persistence;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.ubiqplayer.ui.models.Song;

import java.util.List;

public class PlaylistWithSongs {

    @Embedded
    public Playlist playlist;

    @Relation(
            parentColumn = "playlistId",
            entity = Song.class,
            entityColumn = "songUri",
            associateBy = @Junction(
                    value = PlaylistSongCrossRef.class,
                    parentColumn = "playlistId",
                    entityColumn = "songUri")
    )
    public List<Song> songs;

}
