package com.example.ubiqplayer.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.ui.interfaces.IPlaylistClickListener;
import com.example.ubiqplayer.ui.viewholders.PlaylistViewHolder;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<PlaylistWithSongs> playlists = new ArrayList<>();
    private IPlaylistClickListener playlistClickListener;

    public PlaylistsAdapter(IPlaylistClickListener playlistClickListener) {
        this.playlistClickListener = playlistClickListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View playlistView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PlaylistViewHolder(playlistView, playlistClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaylistViewHolder playlistViewHolder = (PlaylistViewHolder) holder;
        PlaylistWithSongs playlist = playlists.get(position);
        playlistViewHolder.playlistTitleView.setText(playlist.playlist.playlistName);
        playlistViewHolder.playlistNumItems.setText(App.get().getResources().getQuantityString(R.plurals.songs_in_playlist_num, playlist.songs.size(), playlist.songs.size()));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.playlist_item;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<PlaylistWithSongs> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return playlists.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
