package com.example.ubiqplayer.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.R;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.viewholders.PlaylistSongViewHolder;

public class PlaylistWithSongsAdapter extends HomeAdapter{

    public PlaylistWithSongsAdapter(Context context, ISongClickListener songClickListener) {
        super(context, songClickListener);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View songView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PlaylistSongViewHolder(songView, songClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PlaylistSongViewHolder songHolder = (PlaylistSongViewHolder) holder;
        songHolder.removeSongButton.setOnClickListener(v -> songClickListener.removeSong(songHolder.song));
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.playlist_song_item;
    }
}
