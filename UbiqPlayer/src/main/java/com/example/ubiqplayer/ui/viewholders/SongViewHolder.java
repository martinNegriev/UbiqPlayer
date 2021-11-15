package com.example.ubiqplayer.ui.viewholders;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.mediaplayer.MediaPlayerActivity;

public class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public AppCompatImageView thumbnailView;
    public TextView titleView;
    public TextView artistView;
    public TextView durationView;

    public SongViewHolder(@NonNull View itemView) {
        super(itemView);
        thumbnailView = itemView.findViewById(R.id.song_thumbnail);
        titleView = itemView.findViewById(R.id.song_title);
        artistView = itemView.findViewById(R.id.song_artist);
        durationView = itemView.findViewById(R.id.song_duration);
    }

    public Bitmap loadThumbnail(Uri uri) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art = null;
        BitmapFactory.Options bfo=new BitmapFactory.Options();

        mmr.setDataSource(App.get(), uri);
        rawArt = mmr.getEmbeddedPicture();

        if (rawArt != null)
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
        if (art != null)
            return art;
        return null;
    }

    public void setThumbnail(Bitmap bitmap) {
        if (bitmap == null) {
            thumbnailView.setImageResource(R.drawable.ic_audio_file);
            return;
        }
        thumbnailView.setImageBitmap(bitmap);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), MediaPlayerActivity.class);
        intent.setAction(MediaPlayerActivity.MEDIA_PLAYER_ACTIVITY_ACTION);
        v.getContext().startActivity(intent);
    }
}
