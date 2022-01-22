package com.example.ubiqplayer.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    @Nullable
    protected ISongClickListener songClickListener;
    private static final LruCache<String, Bitmap> memoryCache;

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        memoryCache = new LruCache<String, Bitmap>(maxMemory / 8) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public HomeAdapter(Context context, @Nullable ISongClickListener songClickListener) {
        this.context = context;
        this.songClickListener = songClickListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View songView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new SongViewHolder(songView, songClickListener);
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
        Song currentSong = MediaPlayerService.getCurrentSong();
        if (currentSong != null && MediaPlayerService.getState() != Player.STATE_IDLE && correspondingSong.getSongUri().equals(currentSong.getSongUri())) {
            songHolder.artistView.setTextColor(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()));
            songHolder.titleView.setTextColor(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()));
            songHolder.durationView.setTextColor(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()));
        } else {
            songHolder.artistView.setTextColor(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()));
            songHolder.titleView.setTextColor(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()));
            songHolder.durationView.setTextColor(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()));
        }
        processBitmap(songHolder);
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

    private void processBitmap(SongViewHolder songHolder) {
        Bitmap cached = getBitmapFromMemCache(songHolder.song.getSongUri().toString());
        if (cached != null) {
            songHolder.setThumbnail(cached);
            return;
        }
        songHolder.setThumbnail(null);
        new ResultTask<Bitmap>() {
            @Override
            protected Bitmap doInBackground() {
                Bitmap b = songHolder.loadThumbnail(songHolder.song.getSongUri());
                if (b != null)
                    addBitmapToMemoryCache(songHolder.song.getSongUri().toString(), b);

                return b;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                songHolder.setThumbnail(bitmap);
            }
        }.start();
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    @Override
    public long getItemId(int position) {
        return songs.get(position).hashCode();
    }
}
