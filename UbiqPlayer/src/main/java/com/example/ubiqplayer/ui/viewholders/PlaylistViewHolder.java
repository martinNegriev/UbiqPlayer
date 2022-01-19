package com.example.ubiqplayer.ui.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.R;
import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.ui.interfaces.IPlaylistClickListener;

public class PlaylistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private IPlaylistClickListener playlistClickListener;
    public TextView playlistTitleView;
    public TextView playlistNumItems;
    public PlaylistWithSongs playlist;

    public PlaylistViewHolder(@NonNull View itemView, IPlaylistClickListener playlistClickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.playlistClickListener = playlistClickListener;
        playlistTitleView = itemView.findViewById(R.id.playlist_title);
        playlistNumItems = itemView.findViewById(R.id.number_of_songs);
    }

    @Override
    public void onClick(View v) {
        playlistClickListener.onClick(playlist);
    }
}
