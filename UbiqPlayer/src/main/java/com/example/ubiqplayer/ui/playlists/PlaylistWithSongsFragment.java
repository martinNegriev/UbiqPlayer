package com.example.ubiqplayer.ui.playlists;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.BaseFragment;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.mediaplayer.UbiqPlayerLogic;
import com.example.ubiqplayer.persistence.PlaylistSongCrossRef;
import com.example.ubiqplayer.persistence.SongDatabase;
import com.example.ubiqplayer.ui.adapters.PlaylistWithSongsAdapter;
import com.example.ubiqplayer.ui.helper.ResultTask;
import com.example.ubiqplayer.ui.interfaces.ISongAddedListener;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.sorting.SortExtras;
import com.example.ubiqplayer.ui.sorting.SortLocation;
import com.example.ubiqplayer.ui.sorting.SortOption;
import com.example.ubiqplayer.ui.viewmodels.PlaylistWithSongsViewModel;
import com.example.ubiqplayer.utils.CommonUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PlaylistWithSongsFragment extends BaseFragment implements ISongClickListener, ISongAddedListener {

    private View emptyLayout;
    private ImageView addSongsButton;
    private String playlistName;
    private View recyclerViewLayout;
    private MaterialButton addSongsLowerButton;
    private PlaylistWithSongsViewModel viewModel;
    private PlaylistWithSongsAdapter adapter;
    private RecyclerView recyclerView;
    private View sortLayout;
    private TextView sortBy;
    private ImageView sortDirection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        viewModel = new ViewModelProvider(this).get(PlaylistWithSongsViewModel.class);
        observe();
        if (args == null)
            return;
        playlistName = args.getString(PlaylistsFragment.PLAYLIST_NAME_EXTRA);
        if (TextUtils.isEmpty(playlistName))
            return;
        getUbiqPlayerActivity().getSupportActionBar().setTitle(playlistName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist_with_songs, container, false);
        emptyLayout = v.findViewById(R.id.no_songs_in_playlist_layout);
        addSongsButton = v.findViewById(R.id.add_songs_button);
        addSongsLowerButton = v.findViewById(R.id.add_songs_lower_button);
        recyclerViewLayout = v.findViewById(R.id.recycler_view_layout);
        recyclerView = v.findViewById(R.id.home_recycler_view);
        recyclerView.setItemAnimator(null);
        sortLayout = v.findViewById(R.id.sort_layout);
        sortBy = sortLayout.findViewById(R.id.sorted_by);
        sortDirection = sortLayout.findViewById(R.id.sort_direction);
        sortLayout.setOnClickListener(v1 -> openSortDialog(SortLocation.PlaylistWithSong));
        addSongsButton.setOnClickListener(v1 -> openPickSongsFragment(this, playlistName, false));
        addSongsLowerButton.setOnClickListener(v1 -> openPickSongsFragment(this, playlistName, false));
        initRecyclerView();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        applySortAndLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UbiqPlayerLogic playerLogic = getUbiqPlayerActivity().getPlayerLogic();
        if (playerLogic != null) {
            Fragment curFrag = playerLogic.getCurrentFragment();
            if (curFrag instanceof BaseFragment && !TextUtils.isEmpty(((BaseFragment)curFrag).getLocationName())) {
                getUbiqPlayerActivity().getSupportActionBar().setTitle(((BaseFragment)curFrag).getLocationName());
            }
            if (curFrag instanceof PlaylistsFragment)
                ((PlaylistsFragment) curFrag).reloadData();
        }
        getActivity().findViewById(R.id.content_layout).setVisibility(View.GONE);
    }

    @Override
    protected boolean isNavChildFragment() {
        return false;
    }

    protected void observe() {
        // Start observing
        viewModel.getSongsData().observe(this, songs -> {
            if (songs == null || songs.isEmpty()) {
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerViewLayout.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.GONE);
                recyclerViewLayout.setVisibility(View.VISIBLE);
            }
            adapter.updateData(songs);
        });
    }

    private void initRecyclerView() {
        adapter = new PlaylistWithSongsAdapter(getContext(), this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
        return adapter;
    }

    @Override
    public void onClick(@NonNull Song playedSong) {
        List<Song> songs = viewModel.getSongsData().getValue();
        if (songs == null)
            return;
        getUbiqPlayerActivity().startPlayback(playedSong, songs);
    }

    @Override
    public void onSongAdded() {
        applySortAndLoad();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void removeSong(@NonNull Song song) {
        new ResultTask<Integer>() {

            @Override
            protected Integer doInBackground() {
                PlaylistSongCrossRef crossRef = new PlaylistSongCrossRef();
                crossRef.playlistName = playlistName;
                crossRef.songUri = song.getSongUri();
                return SongDatabase.getInstance().songDao().deleteSongFromPlaylist(crossRef);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (integer <= 0)
                    return;
                applySortAndLoad();
            }
        }.start();
    }

    @Override
    protected void applySortAndLoad() {
        SortOption option = SortOption.getByName(CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getString(SortExtras.SORT_EXTRA_PLAYLISTS_WITH_SONGS, SortOption.Title.name()));
        boolean reversed = CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getBoolean(SortExtras.SORT_EXTRA_PLAYLISTS_WITH_SONGS_REVERSED, false);
        App.HANDLER.post(() -> {
            sortBy.setText(option.getName());
            sortDirection.setImageDrawable(ResourcesCompat.getDrawable(App.get().getResources(), reversed ? R.drawable.ic_desc : R.drawable.ic_asc, getContext().getTheme()));
            sortDirection.setColorFilter(getContext().getResources().getColor(R.color.textColor, getContext().getTheme()), PorterDuff.Mode.SRC_IN);
        });
        viewModel.loadSongsForPlaylist(playlistName, option, reversed);
    }
}
