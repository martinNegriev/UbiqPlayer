package com.example.ubiqplayer.ui.viewholders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.ubiqplayer.R;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;

public class PlaylistSongViewHolder extends SongViewHolder{

    public ImageView removeSongButton;

    public PlaylistSongViewHolder(@NonNull View itemView, ISongClickListener songClickListener) {
        super(itemView, songClickListener);
        removeSongButton = itemView.findViewById(R.id.remove_song_button);
    }
}
