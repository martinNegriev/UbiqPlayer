package com.example.ubiqplayer.ui.models;

import android.net.Uri;

public class Song {
    private String title;
    private long duration;
    private String mimetype;
    private Uri uri;

    public Song(String title, long duration, String mimetype, Uri uri) {
        this.title = title;
        this.duration = duration;
        this.mimetype = mimetype;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
