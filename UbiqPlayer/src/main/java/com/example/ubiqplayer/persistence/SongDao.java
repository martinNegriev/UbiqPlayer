package com.example.ubiqplayer.persistence;

import android.net.Uri;

import androidx.room.Dao;
import androidx.room.Delete;
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

    //////////////////////// INSERT

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertPlaylistSongCrossRef(List<PlaylistSongCrossRef> playlistAndSong);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract List<Long> insertSongs(List<Song> songs);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertPlaylists(List<Playlist> newPlayLists);


    //////////////////////// UPDATE

    @Update
    public abstract void update(List<Song> songs);

    @Update
    public abstract void update(Song song);

    @Query("UPDATE song SET favorite = :state WHERE songUri = :songUri")
    public abstract int toggleFavorite(int state, Uri songUri);

    @Query("UPDATE song SET timesPlayed = timesPlayed + 1 WHERE songUri = :songUri")
    public abstract int updateTimesPlayed(Uri songUri);

    //////////////////////// DELETE

    @Delete
    public abstract int deleteSongFromPlaylist(PlaylistSongCrossRef crossRef);

    @Delete
    public abstract int deletePlaylist(Playlist playlist);


    //////////////////////// SELECT CUSTOM QUERIES

    @Query("SELECT * FROM Song")
    public abstract List<Song> getSongs();

    @Query("SELECT * FROM Song WHERE songUri = :songUri")
    public abstract Song getSongByUri(Uri songUri);

    @Transaction
    @Query("SELECT * FROM Playlist ORDER BY playlistName ASC")
    public abstract List<PlaylistWithSongs> getPlaylistWithSongs();

    @Transaction
    @Query("SELECT * FROM Playlist WHERE playlistName = :playlistName")
    public abstract PlaylistWithSongs getPlaylistWithSongsByName(String playlistName);

    @Query("SELECT lyrics FROM Song WHERE songUri = :songUri")
    public abstract String getLyrics(Uri songUri);

    @Query("SELECT * FROM Song WHERE favorite = 1")
    public abstract List<Song> getFavoriteSongs();

    @Query("SELECT * FROM playlist WHERE playlistName = :playlistName")
    public abstract Playlist getPlaylistByName(String playlistName);

    @Query("SELECT * FROM Song WHERE timesPlayed ORDER BY timesPlayed DESC")
    public abstract List<Song> getMostPlayedSongs();

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
