package com.example.ubiqplayer.ui.interfaces;

import androidx.lifecycle.MutableLiveData;

import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.sorting.Comparators;
import com.example.ubiqplayer.ui.sorting.SortOption;

import java.util.Collections;
import java.util.List;

public interface ISongRepository {

    default void sortAndPostSongs(SortOption sortOption, List<Song> songs, MutableLiveData<List<Song>> songsData, boolean reversed) {
        if (sortOption == SortOption.Artist)
            songs.sort(new Comparators.ArtistComparator());
        else if (sortOption == SortOption.Title)
            songs.sort(new Comparators.TitleComparator());
        else if (sortOption == SortOption.Duration)
            songs.sort(new Comparators.DurationComparator());

        if (reversed)
            Collections.reverse(songs);

        songsData.postValue(songs);
    }
}
