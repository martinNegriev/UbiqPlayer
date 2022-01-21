package com.example.ubiqplayer.ui.sorting;

import android.content.Context;

import androidx.annotation.Nullable;

public class SortRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {
    @Nullable
    private SortOption sortOption;

    @Nullable
    private SortDirection sortDirection;

    public SortRadioButton(Context context) {
        super(context);
    }

    @Nullable
    public SortOption getSortOption() {
        return sortOption;
    }

    public void setSortOption(@Nullable SortOption sortOption) {
        this.sortOption = sortOption;
    }

    @Nullable
    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(@Nullable SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }
}
