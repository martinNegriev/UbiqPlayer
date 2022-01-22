package com.example.ubiqplayer.ui.playlists;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.persistence.PlaylistSongCrossRef;
import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.persistence.SongDatabase;
import com.example.ubiqplayer.ui.adapters.HomeAdapter;
import com.example.ubiqplayer.ui.interfaces.ISongAddedListener;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.queue.QueueFragment;
import com.example.ubiqplayer.ui.viewmodels.HomeViewModel;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.Collections;
import java.util.List;

public class PickSongsFragment extends DialogFragment implements ISongClickListener {

    private HomeViewModel songViewModel;
    private RecyclerView recyclerView;
    private HomeAdapter songAdapter;
    private String playlistName;
    private ISongAddedListener songAddedListener;

    public PickSongsFragment(ISongAddedListener songAddedListener) {
        super();
        this.songAddedListener = songAddedListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        playlistName = getArguments().getString(PlaylistsFragment.PLAYLIST_NAME_EXTRA, "");

        // Start observing
        observe();
    }

    private void observe() {
        songViewModel.getSongsData().observe(this, songs -> songAdapter.updateData(songs));
    }

    private void initRecyclerView() {
        songAdapter = new HomeAdapter(getContext(), this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pick_song_dialog_layout, container, false);
        recyclerView = root.findViewById(R.id.pick_songs_recyclerview);
        recyclerView.setItemAnimator(null);
        initRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        String playlistName = getArguments().getString(PlaylistsFragment.PLAYLIST_NAME_EXTRA, "");
        boolean isQueue = getArguments().getBoolean(QueueFragment.QUEUE_EXTRA, false);
        if (TextUtils.isEmpty(playlistName) && !isQueue)
            return;
        songViewModel.loadSongsData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(@NonNull Song clickedSong) {
        CommonUtils.UNBOUNDED_EXECUTOR.execute(() -> {
            if (TextUtils.isEmpty(playlistName)) {
                boolean isQueue = getArguments().getBoolean(QueueFragment.QUEUE_EXTRA, false);
                if (!isQueue)
                    return;
                App.HANDLER.post(() -> {
                    MediaPlayerService.addMediaItem(clickedSong);
                    songAddedListener.onSongAdded();
                });
                return;
            }
            PlaylistWithSongs playlistWithSongs = SongDatabase.getInstance().songDao().getPlaylistWithSongsByName(playlistName);
            List<Song> songs = playlistWithSongs.songs;
            for (Song s : songs) {
                Uri songUri = s.getSongUri();
                if (songUri.equals(clickedSong.getSongUri())) {
                    App.HANDLER.post(() -> Toast.makeText(App.get(), R.string.song_already_added, Toast.LENGTH_LONG).show());
                    return;
                }
            }
            PlaylistSongCrossRef crossRef = new PlaylistSongCrossRef();
            crossRef.playlistName = playlistName;
            crossRef.songUri = clickedSong.getSongUri();
            SongDatabase.getInstance().songDao().insertPlaylistSongCrossRef(Collections.singletonList(crossRef));
            songAddedListener.onSongAdded();
        });
    }
}
