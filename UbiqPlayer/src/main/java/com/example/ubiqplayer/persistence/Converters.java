package com.example.ubiqplayer.persistence;

import android.net.Uri;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public String uriToString(Uri uri) {
        return uri == null ? null : uri.toString();
    }

    @TypeConverter
    public Uri stringToUri(String str) {
        return str == null ? null : Uri.parse(str);
    }
}
