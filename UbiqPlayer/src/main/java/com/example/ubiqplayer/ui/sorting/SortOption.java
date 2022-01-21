package com.example.ubiqplayer.ui.sorting;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;

public enum SortOption {
    Name(App.get().getResources().getString(R.string.sort_by_name)),
    NumberOfSongs(App.get().getResources().getString(R.string.sort_by_playlistSize)),
    Artist(App.get().getResources().getString(R.string.sort_by_artist)),
    Title(App.get().getResources().getString(R.string.sort_by_title)),
    Duration(App.get().getResources().getString(R.string.sort_by_duration));

    private final String name;
    SortOption(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SortOption getByName(String name) {
        if (Name.name().equals(name))
            return Name;
        if (NumberOfSongs.name().equals(name))
            return NumberOfSongs;
        if (Artist.name().equals(name))
            return Artist;
        if (Title.name().equals(name))
            return Title;
        return Duration;
    }
}
