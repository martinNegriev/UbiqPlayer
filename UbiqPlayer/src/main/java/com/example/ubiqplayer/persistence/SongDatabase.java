package com.example.ubiqplayer.persistence;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.ui.models.Song;

@Database(entities = {Song.class, Playlist.class, PlaylistSongCrossRef.class}, exportSchema = false, version = 1)
@TypeConverters({Converters.class})
public abstract class SongDatabase extends RoomDatabase {
    private static final String DB_NAME = "song_db";
    private static SongDatabase instance;

    public static SongDatabase getInstance() {
        if (instance == null) {
            synchronized (SongDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(App.get(), SongDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
                    return instance;
                }
            }
        }
        return instance;
    }

    public abstract SongDao songDao();
}
