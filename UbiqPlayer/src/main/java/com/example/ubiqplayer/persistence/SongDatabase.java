package com.example.ubiqplayer.persistence;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.ui.models.Song;

@Database(entities = {Song.class, Playlist.class, PlaylistSongCrossRef.class}, exportSchema = false, version = 2)
@TypeConverters({Converters.class})
public abstract class SongDatabase extends RoomDatabase {
    private static final String DB_NAME = "song_db";
    private static SongDatabase instance;

    public static SongDatabase getInstance() {
        if (instance == null) {
            synchronized (SongDatabase.class) {
                if (instance == null) {

                    instance = Room.databaseBuilder(App.get(), SongDatabase.class, DB_NAME)
                            .fallbackToDestructiveMigration()
                            .addMigrations(MIGRATION_1_2).build();
                    return instance;
                }
            }
        }
        return instance;
    }

    public abstract SongDao songDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE song ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
        }
    };
}
