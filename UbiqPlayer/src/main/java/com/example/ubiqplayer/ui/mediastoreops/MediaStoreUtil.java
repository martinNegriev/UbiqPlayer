package com.example.ubiqplayer.ui.mediastoreops;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.persistence.SongDatabase;
import com.example.ubiqplayer.ui.models.Song;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MediaStoreUtil {

    public static List<Song> querySongs() {
        Map<Uri, Song> songs = new LinkedHashMap<>();
        Uri collectionUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            collectionUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else
            collectionUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.MIME_TYPE
        };
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        try(Cursor cursor = App.get().getContentResolver().query(collectionUri, projection, null, new String[]{}, sortOrder)) {
            int colIndId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int colAlbumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int colIndName = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int colIndTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
            int colArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int colIndDur = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int colIndSize = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int colIndMType = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(colIndId);
                long albumId = cursor.getLong(colAlbumId);
                String displayName = cursor.getString(colIndName);
                long duration = cursor.getLong(colIndDur);
                long size = cursor.getLong(colIndSize);
                String mimeType = cursor.getString(colIndMType);
                String title = cursor.getString(colIndTitle);
                String artist = cursor.getString(colArtist);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                Song song = new Song(title, displayName, artist, duration, mimeType, contentUri, size);
                song.setAlbumId(albumId);
                songs.put(contentUri, song);
            }
        }

        updatePartial(songs);
        List<Song> songsList = new ArrayList<>(songs.values());
        SongDatabase.getInstance().songDao().upsert(songsList);

        return songsList;
    }

    public static List<Song> queryFavorites() {
        List<Song> favs = new ArrayList<>();
        List<Song> cachedFavs = SongDatabase.getInstance().songDao().getFavoriteSongs();
        if (cachedFavs != null)
            favs.addAll(cachedFavs);
        return favs;
    }

    public static List<PlaylistWithSongs> queryPlaylists() {
        List<PlaylistWithSongs> playlists = new ArrayList<>();
        List<PlaylistWithSongs> cachedPlaylists = SongDatabase.getInstance().songDao().getPlaylistWithSongs();
        if (cachedPlaylists != null)
            playlists.addAll(cachedPlaylists);
        return playlists;
    }

    private static void updatePartial(Map<Uri, Song> newSongs) {
        List<Song> cachedSongs = SongDatabase.getInstance().songDao().getSongs();
        if (cachedSongs == null)
            return;
        for (Song cachedSong : cachedSongs) {
            Song newSong = newSongs.get(cachedSong.getSongUri());
            if (newSong != null) {
                //TODO Add playlist stuff maaybe?
                newSong.setFavorite(cachedSong.isFavorite());
                newSong.setLyrics(cachedSong.getLyrics());
            }
        }
    }
}
