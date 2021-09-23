package com.example.ubiqplayer.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.R;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.viewholders.SongViewHolder;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Song> songs = new ArrayList<>();

    public HomeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View songView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new SongViewHolder(songView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //TODO
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.home_item;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }
}
