package com.example.ubiqplayer.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.R;
import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.ui.helper.ResultTask;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.viewholders.SongViewHolder;
import com.example.ubiqplayer.utils.SongUtils;
import com.google.android.exoplayer2.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Song> songs = new ArrayList<>();
    private ISongClickListener songClickListener;

    public HomeAdapter(Context context, ISongClickListener songClickListener) {
        this.context = context;
        this.songClickListener = songClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View songView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        SongViewHolder holder = new SongViewHolder(songView, songClickListener);
        return holder;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Context ctx = holder.itemView.getContext();
        SongViewHolder songHolder = (SongViewHolder) holder;
        Song correspondingSong = songs.get(position);
        songHolder.artistView.setText(correspondingSong.getArtist());
        songHolder.titleView.setText(correspondingSong.getTitle());
        songHolder.durationView.setText(SongUtils.getFormattedDuration(correspondingSong.getDuration()));
        songHolder.song = correspondingSong;
        if (MediaPlayerService.getState() != Player.STATE_IDLE && correspondingSong.getUri().equals(MediaPlayerService.getCurrentSong().getUri())) {
            songHolder.artistView.setTextColor(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()));
            songHolder.titleView.setTextColor(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()));
            songHolder.durationView.setTextColor(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()));
        } else {
            songHolder.artistView.setTextColor(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()));
            songHolder.titleView.setTextColor(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()));
            songHolder.durationView.setTextColor(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()));
        }
        new ResultTask<Bitmap>() {
            @Override
            protected Bitmap doInBackground() {
                Bitmap b = songHolder.loadThumbnail(correspondingSong.getUri());
                songHolder.song.setThumb(b);
                return b;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                songHolder.setThumbnail(bitmap);
            }
        }.start();
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
