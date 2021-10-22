package com.example.ubiqplayer.utils;

import java.util.Locale;

public class SongUtils {

    public static String getFormattedDuration(long timeMs) {
        if (timeMs <= 0)
            return "--:--";
        return stringForTime(timeMs);
    }

    public static String stringForTime(long timeMs) {
        if (timeMs < 0)
            return "";

        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours   = totalSeconds / 3600;

        if (hours > 0)
            return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format(Locale.ENGLISH,"%02d:%02d", minutes, seconds);
    }
}
