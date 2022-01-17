package com.example.ubiqplayer.ui.models;

import android.net.Uri;

import androidx.room.Entity;

@Entity
public class Song {
    private Uri songUri;
    private String title;
    private String artist;
    private long duration;
    private String mimetype;
    private long size;
    private long albumId;
    private String displayName;

    public Song(String title, String displayName, String artist, long duration, String mimetype, Uri songUri, long size) {
        this.title = title;
        this.artist = artist;
        this.displayName = displayName;
        this.duration = duration;
        this.mimetype = mimetype;
        this.songUri = songUri;
        this.size = size;
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

    public Uri getSongUri() {
        return songUri;
    }

    public void setSongUri(Uri songUri) {
        this.songUri = songUri;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
