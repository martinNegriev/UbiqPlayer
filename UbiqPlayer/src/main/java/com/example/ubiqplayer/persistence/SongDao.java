package com.example.ubiqplayer.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.ubiqplayer.ui.models.Song;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class SongDao {

    @Query("SELECT * FROM Song")
    public abstract List<Song> getSongs();

    @Transaction
    @Query("SELECT * FROM Playlist")
    public abstract List<PlaylistWithSongs> getPlaylistWithSongs();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract List<Long> insertSongs(List<Song> songs);

    @Update
    public abstract void update(List<Song> songs);

    @Update
    public abstract void update(Song song);

    @Transaction
    public void upsert(List<Song> songs) {
        List<Long> insertResult = insertSongs(songs);
        List<Song> updateList = new ArrayList<>();

        for (int i = 0; i < insertResult.size(); i++) {
            if (insertResult.get(i) == -1)
                updateList.add(songs.get(i));
        }

        if (!updateList.isEmpty()) {
            update(updateList);
        }
    }
}
