package com.example.ubiqplayer;

import android.annotation.SuppressLint;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.ui.sorting.SortDirection;
import com.example.ubiqplayer.ui.sorting.SortExtras;
import com.example.ubiqplayer.ui.sorting.SortLocation;
import com.example.ubiqplayer.ui.sorting.SortOption;
import com.example.ubiqplayer.ui.sorting.SortRadioButton;
import com.example.ubiqplayer.utils.CommonUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFragment extends Fragment {

    public UbiqPlayerActivity getUbiqPlayerActivity() {
        return (UbiqPlayerActivity) getActivity();
    }

    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterDataSetChanged() {
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = getAdapter();
        if (adapter == null)
            return;
        adapter.notifyDataSetChanged();
    }

    protected boolean isNavChildFragment() {
        return false;
    }

    @NonNull
    public String getLocationName() {
        return "";
    }

    protected void openSortDialog(SortLocation sortLocation) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.MultiChoiceAlertDialog);
        View dialogContent = getUbiqPlayerActivity().getLayoutInflater().inflate(R.layout.radio_button_dialog_content, null, false);
        RadioGroup sortTypeRadioGroup = dialogContent.findViewById(R.id.sort_by_type);
        RadioGroup sortDirectionRadioGroup = dialogContent.findViewById(R.id.sort_by_direction);
        int activeColor = getContext().getResources().getColor(R.color.dialogActiveTextColor, getContext().getTheme());
        int inactiveColor = getContext().getResources().getColor(R.color.textColor, getContext().getTheme());
        ContextThemeWrapper radioStyleWrapper = new ContextThemeWrapper(getContext(), R.style.MyRadioButton);

        List<SortOption> sortOptions = new ArrayList<>();
        if (sortLocation == SortLocation.Playlist) {
            sortOptions.add(SortOption.Name);
            sortOptions.add(SortOption.NumberOfSongs);
        } else if (sortLocation == SortLocation.Song || sortLocation == SortLocation.PlaylistWithSong) {
            sortOptions.add(SortOption.Title);
            sortOptions.add(SortOption.Artist);
            sortOptions.add(SortOption.Duration);
        }

        for (SortOption s : sortOptions) {
            SortRadioButton radioButton = new SortRadioButton(radioStyleWrapper);
            radioButton.setText(s.getName());
            radioButton.setSortOption(s);
            sortTypeRadioGroup.addView(radioButton);
        }

        SortRadioButton ascButton = new SortRadioButton(radioStyleWrapper);
        ascButton.setText(App.get().getResources().getString(R.string.sort_by_asc));
        ascButton.setSortDirection(SortDirection.Asc);

        SortRadioButton descButton = new SortRadioButton(radioStyleWrapper);
        descButton.setText(App.get().getResources().getString(R.string.sort_by_desc));
        descButton.setSortDirection(SortDirection.Desc);

        sortDirectionRadioGroup.addView(ascButton);
        sortDirectionRadioGroup.addView(descButton);

        setRadioButtonsListener(sortLocation, sortTypeRadioGroup, activeColor, inactiveColor);
        setRadioButtonsListener(sortLocation, sortDirectionRadioGroup, activeColor, inactiveColor);

        for (int i = 0; i < sortTypeRadioGroup.getChildCount(); i++) {
            SortRadioButton radioButton = (SortRadioButton) sortTypeRadioGroup.getChildAt(i);
            setButtonChecked(sortLocation, radioButton);
        }


        for (int i = 0; i < sortDirectionRadioGroup.getChildCount(); i++) {
            SortRadioButton radioButton = (SortRadioButton) sortDirectionRadioGroup.getChildAt(i);
            setButtonChecked(sortLocation, radioButton);
        }

        builder.setTitle(App.get().getResources().getString(R.string.sort_options));
        builder.setOnDismissListener(dialog -> applySortAndLoad());
        builder.setView(dialogContent);
        builder.show();
    }

    private void setRadioButtonsListener(SortLocation sortLocation, RadioGroup radioGroup, int activeColor, int inactiveColor) {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                SortRadioButton button = (SortRadioButton) radioGroup.getChildAt(i);
                if (button.getId() == checkedId) {
                    SortOption sortOption= button.getSortOption();
                    SortDirection sortDirection = button.getSortDirection();
                    processSortChange(sortLocation, sortOption, sortDirection);
                    button.setTextColor(activeColor);
                } else
                    button.setTextColor(inactiveColor);
            }
        });
    }

    private void processSortChange(SortLocation sortLocation, @Nullable SortOption sortOption, @Nullable SortDirection sortDirection) {
        if (sortLocation == SortLocation.Playlist) {
            if (sortOption != null)
                CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).edit().putString(SortExtras.SORT_EXTRA_PLAYLISTS, sortOption.name()).apply();
            if (sortDirection != null)
                CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).edit().putBoolean(SortExtras.SORT_EXTRA_PLAYLISTS_REVERSED, sortDirection == SortDirection.Desc).apply();
        } else if (sortLocation == SortLocation.Song) {
            if (sortOption != null)
                CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).edit().putString(SortExtras.SORT_EXTRA_SONGS, sortOption.name()).apply();
            if (sortDirection != null)
                CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).edit().putBoolean(SortExtras.SORT_EXTRA_SONGS_REVERSED, sortDirection == SortDirection.Desc).apply();
        } else if (sortLocation == SortLocation.PlaylistWithSong) {
            if (sortOption != null)
                CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).edit().putString(SortExtras.SORT_EXTRA_PLAYLISTS_WITH_SONGS, sortOption.name()).apply();
            if (sortDirection != null)
                CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).edit().putBoolean(SortExtras.SORT_EXTRA_PLAYLISTS_WITH_SONGS_REVERSED, sortDirection == SortDirection.Desc).apply();
        }
    }

    private void setButtonChecked(SortLocation sortLocation, SortRadioButton radioButton) {
        SortOption option = radioButton.getSortOption();
        SortDirection direction = radioButton.getSortDirection();
        if (sortLocation == SortLocation.Playlist) {
            checkPrefs(option, direction, radioButton, SortExtras.SORT_EXTRA_PLAYLISTS, SortExtras.SORT_EXTRA_PLAYLISTS_REVERSED);
            return;
        }
        if (sortLocation == SortLocation.Song) {
            checkPrefs(option, direction, radioButton, SortExtras.SORT_EXTRA_SONGS, SortExtras.SORT_EXTRA_SONGS_REVERSED);
            return;
        }
        if (sortLocation == SortLocation.PlaylistWithSong)
            checkPrefs(option, direction, radioButton, SortExtras.SORT_EXTRA_PLAYLISTS_WITH_SONGS, SortExtras.SORT_EXTRA_PLAYLISTS_WITH_SONGS_REVERSED);
    }

    private void checkPrefs(@Nullable SortOption option, @Nullable SortDirection direction, SortRadioButton radioButton, @NonNull String extraSort, @NonNull String extraDirection) {
        if (option != null) {
            SortOption defaultSortOption = SortOption.Title;
            if (extraSort.equals(SortExtras.SORT_EXTRA_PLAYLISTS))
                defaultSortOption = SortOption.Name;
            if (option.name().equals(CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getString(extraSort, defaultSortOption.name())))
                radioButton.setChecked(true);
        }
        if (direction != null) {
            boolean isReversed = CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getBoolean(extraDirection, false);
            if (isReversed && (direction == SortDirection.Desc))
                radioButton.setChecked(true);
            else if (!isReversed && (direction == SortDirection.Asc))
                radioButton.setChecked(true);
        }
    }

    protected void applySortAndLoad(){}
}
