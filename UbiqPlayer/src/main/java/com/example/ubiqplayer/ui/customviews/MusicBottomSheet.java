package com.example.ubiqplayer.ui.customviews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatDrawableManager;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.databinding.MusicBottomSheetLayoutBinding;
import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.mediaplayer.lyricsfinder.LyricsFinder;
import com.example.ubiqplayer.persistence.SongDatabase;
import com.example.ubiqplayer.ui.adapters.HomeAdapter;
import com.example.ubiqplayer.ui.helper.ResultTask;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.utils.SongUtils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.ubiqplayer.mediaplayer.lyricsfinder.LyricsFinder.MIN_LENGTH_LYRICS;

public class MusicBottomSheet {

    private MusicBottomSheetLayoutBinding binding;
    private View bottomSheet;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private boolean isDragging;
    private final Activity act;
    @Nullable
    private Song playingSong;

    private static final int SHOW_PROGRESS = 1111;
    private static final long PROGRESS_MAX_VALUE = 1000;

    @SuppressLint("RestrictedApi")
    private final static Drawable PLAY_DRAWABLE = AppCompatDrawableManager.get().getDrawable(App.get(), R.drawable.ic_play);
    @SuppressLint("RestrictedApi")
    private final static Drawable PAUSE_DRAWABLE = AppCompatDrawableManager.get().getDrawable(App.get(),  R.drawable.ic_pause);


    public MusicBottomSheet(View bottomSheet, BottomSheetBehavior<LinearLayout> bottomSheetBehavior, Activity act) {
        this.bottomSheet = bottomSheet;
        this.bottomSheetBehavior = bottomSheetBehavior;
        this.binding = MusicBottomSheetLayoutBinding.bind(bottomSheet);
        this.act = act;
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

    @SuppressLint({"StaticFieldLeak", "ClickableViewAccessibility"})
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

        initLyricsMode();
        binding.playerLyricsButton.setOnClickListener(v -> {
            if (MediaPlayerService.getState() == Player.STATE_IDLE)
                return;
            View playerThumb = binding.playerThumb;
            if (playerThumb.getVisibility() == View.GONE) {
                hideLyricsView();
                return;
            }
            seekLyricsInWeb();

            GestureDetector detector = new GestureDetector(binding.bottomSheetContent.getContext(), new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) { }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) { }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    Map<Uri, List<String>> currentSongLyricsList = MediaPlayerService.getCurrentSongLyricsList();
                    Song currentSong = MediaPlayerService.getCurrentSong();
                    String currentLyrics = binding.playerLyricsView.getText().toString();
                    if (currentSongLyricsList == null
                            || currentSong == null
                            || !currentSongLyricsList.keySet().toArray()[0].equals(currentSong.getSongUri())
                            || TextUtils.isEmpty(currentLyrics))
                        return false;
                    List<List<String>> lyricsList = new ArrayList<>(currentSongLyricsList.values());
                    List<String> lyrics = lyricsList.get(0);
                    int ind = lyrics.indexOf(currentLyrics);
                    if (ind < 0)
                        return false;
                    if (velocityX > 0 && ind > 0) {
                        // left
                        String newLyrics = lyrics.get(ind - 1);
                        if (!TextUtils.isEmpty(newLyrics)) {
                            binding.playerLyricsView.setText(newLyrics);
                            currentSong.setLyrics(newLyrics);
                        }
                    } else if (velocityX < 0 && ind < lyrics.size() - 1) {
                        // right
                        String newLyrics = lyrics.get(ind + 1);
                        if (!TextUtils.isEmpty(newLyrics)) {
                            binding.playerLyricsView.setText(newLyrics);
                            currentSong.setLyrics(newLyrics);
                        }
                    }
                    return false;
                }
            });

            binding.playerLyricsView.setOnTouchListener((v1, event) -> {
                if (MotionEvent.ACTION_DOWN == (event.getAction()))
                    binding.bottomSheetContent.requestDisallowInterceptTouchEvent(true);
                if (MotionEvent.ACTION_UP == (event.getAction()))
                    binding.bottomSheetContent.requestDisallowInterceptTouchEvent(false);
                detector.onTouchEvent(event);
                return false;
                }
            );
        });

        binding.playerSaveLyricsButton.setOnClickListener(v -> new Thread(() -> {
            Song currentSong = MediaPlayerService.getCurrentSong();
            String lyrics = SongDatabase.getInstance().songDao().getLyrics(currentSong.getSongUri());
            if (!TextUtils.isEmpty(lyrics)) {
                currentSong.setLyrics(null);
                App.HANDLER.post(() -> binding.playerLyricsButton.callOnClick());
            } else
                MediaPlayerService.setCurrentSongLyricsList(Collections.singletonList(lyrics));
            SongDatabase.getInstance().songDao().update(currentSong);
            initLyricsMode();
        }).start());

        initFavsMode(null);
        binding.playerFavsButton.setOnClickListener(v -> new ResultTask<Long>() {
            Boolean isFav = null;
            @Override
            protected Long doInBackground() {
                Song currentSong = MediaPlayerService.getCurrentSong();
                int state = currentSong.isFavorite() ? 0 : 1;
                long res = SongDatabase.getInstance().songDao().toggleFavorite(state, currentSong.getSongUri());
                if (res > 0) {
                    isFav = state == 1;
                    currentSong.setFavorite(isFav);
                }
                return res;
            }

            @Override
            protected void onPostExecute(Long updated) {
                if (updated <= 0)
                    return;
                initFavsMode(isFav);
                }
        }.start());

        binding.playerRingtoneButton.setOnClickListener(v -> {
            Intent intent=new Intent(Settings.ACTION_SOUND_SETTINGS);
            act.startActivity(intent);
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void seekLyricsInWeb() {
        new ResultTask<List<String>>() {

            @Override
            protected List<String> doInBackground() {
                Song currentSong = MediaPlayerService.getCurrentSong();
                if (currentSong == null)
                    return null;
                String cachedLyrics = SongDatabase.getInstance().songDao().getLyrics(currentSong.getSongUri());
                if (!TextUtils.isEmpty(cachedLyrics)) {
                    List<String> lyricsList = new ArrayList<>();
                    lyricsList.add(cachedLyrics);
                    return lyricsList;
                }
                String artist = currentSong.getArtist();
                String title = currentSong.getTitle();
                String query;
                if (!TextUtils.isEmpty(artist) && !TextUtils.isEmpty(title))
                    query = artist + " " + title;
                else if (!TextUtils.isEmpty(title))
                    query = title;
                else {
                    return null;
                }
                List<String> urls;
                try {
                    urls = LyricsFinder.INSTANCE.find(query + " song lyrics");
                    if (urls == null || urls.isEmpty()) {
                        Toast.makeText(App.get(), R.string.toast_msg_no_lyrics_found, Toast.LENGTH_LONG).show();
                        return null;
                    }
                } catch (Exception e) {
                    Toast.makeText(App.get(), R.string.toast_msg_no_lyrics_found, Toast.LENGTH_LONG).show();
                    return null;
                }
                List<String> lyricsList = new ArrayList<>();
                for (String url : urls) {
                    String lyrics;
                    try {
                        lyrics = LyricsFinder.INSTANCE.findLyrics(url);
                    } catch (Exception e) {
                        continue;
                    }
                    if (lyrics != null && lyrics.length() > MIN_LENGTH_LYRICS)
                        lyricsList.add(lyrics);
                }
                return lyricsList;
            }

            @Override
            protected void onPostExecute(List<String> lyrics) {
                if (lyrics == null || lyrics.isEmpty()) {
                    Toast.makeText(App.get(), R.string.toast_msg_no_lyrics_found, Toast.LENGTH_LONG).show();
                    return;
                }
                MediaPlayerService.setCurrentSongLyricsList(lyrics);
                String firstLyrics = lyrics.get(0);
                MediaPlayerService.getCurrentSong().setLyrics(firstLyrics);
                showLyricsView(firstLyrics);
            }
        }.start();
    }

    public void hideLyricsView() {
        View playerThumb = binding.playerThumb;
        binding.playerLyricsView.setVisibility(View.GONE);
        binding.playerSaveLyricsButton.setVisibility(View.INVISIBLE);
        playerThumb.setVisibility(View.VISIBLE);
        playerThumb.animate()
                .alpha(1.0f)
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        initLyricsMode();
                    }
                });
    }

    private void showLyricsView(String lyrics) {
        View playerThumb = binding.playerThumb;
        playerThumb.animate()
                .alpha(0.0f)
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        playerThumb.setVisibility(View.GONE);
                        binding.playerLyricsView.setText(lyrics);
                        binding.playerLyricsView.setMovementMethod(new ScrollingMovementMethod());
                        binding.playerLyricsView.setVisibility(View.VISIBLE);
                        binding.playerSaveLyricsButton.setVisibility(View.VISIBLE);
                        initLyricsMode();
                    }
                });
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
        initFavsMode(null);
        Bitmap thumb = HomeAdapter.getBitmapFromMemCache(playingSong.getSongUri().toString());
        if (thumb != null)
            binding.playerThumb.setImageBitmap(thumb);
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

    @SuppressLint("StaticFieldLeak")
    private void initLyricsMode() {
        if (MediaPlayerService.getState() == ExoPlayer.STATE_IDLE)
            return;
        Context ctx = binding.playerLyricsButton.getContext();
        boolean isLyricsModeEnabled = binding.playerThumb.getVisibility() == View.GONE;
        if (isLyricsModeEnabled) {
            binding.playerLyricsButton.setColorFilter(ctx.getResources().getColor(R.color.colorAccent, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
            new ResultTask<String>() {

                @Override
                protected String doInBackground() {
                    return SongDatabase.getInstance().songDao().getLyrics(MediaPlayerService.getCurrentSong().getSongUri());
                }

                @Override
                protected void onPostExecute(String cachedLyrics) {
                    int color = !TextUtils.isEmpty(cachedLyrics) ? R.color.colorAccent : R.color.iconInactiveColor;
                    binding.playerSaveLyricsButton.setColorFilter(ctx.getResources().getColor(color, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
                }
            }.start();
            return;
        }
        binding.playerLyricsButton.setColorFilter(ctx.getResources().getColor(R.color.iconInactiveColor, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
    }

    private void initFavsMode(Boolean fav) {
        boolean isFav;
        if (fav != null)
            isFav = fav;
        else {
            Song song = MediaPlayerService.getCurrentSong();
            if (song == null)
                return;
            isFav = song.isFavorite();
        }
        Context ctx = binding.playerFavsButton.getContext();
        binding.playerFavsButton.setColorFilter(ctx.getResources().getColor(isFav ? R.color.colorAccent : R.color.iconInactiveColor, ctx.getTheme()), PorterDuff.Mode.SRC_IN);
    }

}
