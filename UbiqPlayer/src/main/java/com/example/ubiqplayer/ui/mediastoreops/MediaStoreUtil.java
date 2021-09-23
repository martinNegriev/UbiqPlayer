package com.example.ubiqplayer.ui.mediastoreops;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.ui.models.Song;

import java.util.ArrayList;
import java.util.List;

public class MediaStoreUtil {

    public static List<Song> querySongs() {
        List<Song> songs = new ArrayList<>();
        Uri collectionUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            collectionUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        else
            collectionUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.MIME_TYPE
        };
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        try(Cursor cursor = App.get().getContentResolver().query(collectionUri, projection, null, new String[]{}, sortOrder)) {
            int colIndId = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int colIndName = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int colIndDur = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int colIndSize = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int colIndMType = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(colIndId);
                String name = cursor.getString(colIndName);
                long duration = cursor.getLong(colIndDur);
                long size = cursor.getLong(colIndSize);
                String mimeType = cursor.getString(colIndMType);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                songs.add(new Song(name, duration, mimeType, contentUri, size));
            }
        }

        return songs;
    }
}
