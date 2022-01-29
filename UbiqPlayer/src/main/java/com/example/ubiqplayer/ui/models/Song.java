package com.example.ubiqplayer.ui.models;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Song {
    @PrimaryKey
    private @NonNull Uri songUri;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "duration")
    private long duration;

    @ColumnInfo(name = "mimetype")
    private String mimetype;

    @ColumnInfo(name = "size")
    private long size;

    @ColumnInfo(name = "albumId")
    private long albumId;

    @ColumnInfo(name = "displayName")
    private String displayName;

    @ColumnInfo(name = "lyrics")
    private String lyrics;

    @ColumnInfo(name = "favorite")
    private boolean favorite;

    @ColumnInfo(name = "timesPlayed")
    private int timesPlayed;

    public Song(String title, String displayName, String artist, long duration, String mimetype, @NonNull Uri songUri, long size) {
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

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getTimesPlayed() {
        return timesPlayed;
    }

    public void setTimesPlayed(int timesPlayed) {
        this.timesPlayed = timesPlayed;
    }
}
