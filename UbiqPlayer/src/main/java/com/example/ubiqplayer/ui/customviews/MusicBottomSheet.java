package com.example.ubiqplayer.ui.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatDrawableManager;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.databinding.MusicBottomSheetLayoutBinding;
import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.utils.SongUtils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MusicBottomSheet {

    private MusicBottomSheetLayoutBinding binding;
    private View bottomSheet;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private boolean isDragging;
    @Nullable
    private Song playingSong;

    private static final int SHOW_PROGRESS = 1111;
    private static final long PROGRESS_MAX_VALUE = 1000;

    @SuppressLint("RestrictedApi")
    private final static Drawable PLAY_DRAWABLE = AppCompatDrawableManager.get().getDrawable(App.get(), R.drawable.ic_play);
    @SuppressLint("RestrictedApi")
    private final static Drawable PAUSE_DRAWABLE = AppCompatDrawableManager.get().getDrawable(App.get(),  R.drawable.ic_pause);


    public MusicBottomSheet(View bottomSheet, BottomSheetBehavior<LinearLayout> bottomSheetBehavior) {
        this.bottomSheet = bottomSheet;
        this.bottomSheetBehavior = bottomSheetBehavior;
        this.binding = MusicBottomSheetLayoutBinding.bind(bottomSheet);
        binding.playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser)
                    return;
                if (playingSong == null)
                    return;

                long duration = playingSong.getDuration();
                long newposition = (duration * progress) / PROGRESS_MAX_VALUE;
                MediaPlayerService.seek(newposition);
                binding.playerCurrentPos.setText(SongUtils.getFormattedDuration(newposition));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
                handler.removeMessages(SHOW_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                getAndUpdatePlayerCurrentPos();
                handler.sendEmptyMessage(SHOW_PROGRESS);
            }
        });

        initClickListeners();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == SHOW_PROGRESS) {
                long pos = getAndUpdatePlayerCurrentPos();
                if(!isDragging && MediaPlayerService.isPlaying()){
                    msg = obtainMessage(SHOW_PROGRESS);
                    sendMessageDelayed(msg, 250 - (pos % 250));
                }
            }
        }

    };

    private void initClickListeners() {
        binding.playerPlayPause.setOnClickListener(v -> {
            if (MediaPlayerService.getState() == ExoPlayer.STATE_IDLE)
                return;
            MediaPlayerService.togglePlayPausePlayer();
        });

        binding.playerNext.setOnClickListener(v -> {
            if (MediaPlayerService.getState() == ExoPlayer.STATE_IDLE)
                return;
            MediaPlayerService.playNextPrev(true);
        });

        binding.playerPrev.setOnClickListener(v -> {
            if (MediaPlayerService.getState() == ExoPlayer.STATE_IDLE)
                return;
            MediaPlayerService.playNextPrev(false);
        });

        initRepeatMode(false);
        binding.playerRepeat.setOnClickListener(v -> initRepeatMode(true));

        initShuffleMode(false);
        binding.playerShuffle.setOnClickListener(v -> initShuffleMode(true));
    }

    public void toggleBottomSheet() {
        int state = bottomSheetBehavior.getState();
        if (state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void hideBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
            return;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @SuppressLint("RestrictedApi")
    public void refreshUI() {
        playingSong = MediaPlayerService.getCurrentSong();
        binding.playerArtist.setText(playingSong.getArtist());
        binding.playerTitle.setText(playingSong.getTitle());
        binding.playerDuration.setText(SongUtils.getFormattedDuration(playingSong.getDuration()));
        binding.playerCurrentPos.setText(SongUtils.getFormattedDuration(MediaPlayerService.getPlaybackPosition()));
        initShuffleMode(false);
        initRepeatMode(false);
        Bitmap thumb = playingSong.getThumb();
        if (thumb != null)
            binding.playerThumb.setImageBitmap(playingSong.getThumb());
        else
            binding.playerThumb.setImageDrawable(AppCompatDrawableManager.get().getDrawable(App.get(), R.drawable.ic_audio_file));
        updatePlayPause();
        handler.sendEmptyMessage(SHOW_PROGRESS);
    }

    private void updatePlayPause() {
        Context ctx = bottomSheet.getContext();
        if (MediaPlayerService.isPlaying()) {
            PAUSE_DRAWABLE.setColorFilter(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
            binding.playerPlayPause.setImageDrawable(PAUSE_DRAWABLE);
        } else {
            PLAY_DRAWABLE.setColorFilter(ctx.getResources().getColor(R.color.textColor, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
            binding.playerPlayPause.setImageDrawable(PLAY_DRAWABLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private long getAndUpdatePlayerCurrentPos() {
        if (isDragging)
            return 0;
        if (playingSong == null)
            return 0;
        long curPlayerPos = MediaPlayerService.getPlaybackPosition();
        long dur = playingSong.getDuration();
        binding.playerDuration.setText(SongUtils.getFormattedDuration(dur));
        if (dur > 0)
            binding.playerSeekBar.setProgress((int) (PROGRESS_MAX_VALUE * curPlayerPos / dur));
        else
            binding.playerDuration.setText("00:00");
        binding.playerCurrentPos.setText(SongUtils.getFormattedDuration(curPlayerPos));
        return curPlayerPos;
    }

    private void initRepeatMode(boolean toggle) {
        if (MediaPlayerService.getState() == ExoPlayer.STATE_IDLE)
            return;
        int repeatMode = MediaPlayerService.toggleAndGetRepeatMode(toggle);
        Context ctx = binding.playerRepeat.getContext();
        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF: {
                binding.repeatOne.setVisibility(View.INVISIBLE);
                binding.playerRepeat.setColorFilter(ctx.getResources().getColor(R.color.iconInactiveColor, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ONE: {
                binding.repeatOne.setVisibility(View.VISIBLE);
                binding.playerRepeat.setColorFilter(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
                break;
            }
            case Player.REPEAT_MODE_ALL: {
                binding.repeatOne.setVisibility(View.INVISIBLE);
                binding.playerRepeat.setColorFilter(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private void initShuffleMode(boolean toggle) {
        if (MediaPlayerService.getState() == ExoPlayer.STATE_IDLE)
            return;
        Context ctx = binding.playerShuffle.getContext();
        boolean isShuffleModeEnabled = MediaPlayerService.toggleAndGetShuffleMode(toggle);
        if (isShuffleModeEnabled) {
            binding.playerShuffle.setColorFilter(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
            return;
        }
        binding.playerShuffle.setColorFilter(ctx.getResources().getColor(R.color.iconInactiveColor, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
    }

}
