package com.example.ubiqplayer.ui.sorting;

import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.ui.models.Song;

public class Comparators {

    public static class NumSongsComparator implements java.util.Comparator<PlaylistWithSongs> {
        @Override
        public int compare(PlaylistWithSongs o1, PlaylistWithSongs o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            if (o1.equals(o2))
                return 0;
            if (o1.songs.size() >= o2.songs.size())
                return 1;
            return -1;
        }
    }

    public static class ArtistComparator implements java.util.Comparator<Song> {
        @Override
        public int compare(Song o1, Song o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            if (o1.equals(o2))
                return 0;
            int cmp = o1.getArtist().compareTo(o2.getArtist());
            if (cmp >= 0)
                return 1;
            return -1;
        }
    }

    public static class TitleComparator implements java.util.Comparator<Song> {
        @Override
        public int compare(Song o1, Song o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            if (o1.equals(o2))
                return 0;
            int cmp = o1.getTitle().compareTo(o2.getTitle());
            if (cmp >= 0)
                return 1;
            return -1;
        }
    }

    public static class DurationComparator implements java.util.Comparator<Song> {
        @Override
        public int compare(Song o1, Song o2) {
            if (o1 == null)
                return -1;
            if (o2 == null)
                return 1;
            if (o1.equals(o2))
                return 0;
            if (o1.getDuration() >= o2.getDuration())
                return 1;
            return -1;
        }
    }
}
