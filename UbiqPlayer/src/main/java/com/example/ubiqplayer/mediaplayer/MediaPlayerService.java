package com.example.ubiqplayer.mediaplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.utils.CommonUtils;
import com.example.ubiqplayer.utils.MediaPlayerUtils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;

import java.util.List;

public class MediaPlayerService extends LifecycleService {

    private static MediaPlayerService instance = null;

    private static ExoPlayer playerCore;
    private static boolean playWhenReady = true;
    private static MediaItem currentItem = null;
    private static long playbackPosition = 0L;
    private static Song playedSong = null;
    private final IBinder musicBinder = new MusicBinder();
    private static NotificationCompat.Builder notificationBuilder;
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static MediaSessionCompat mediaSession;
    private static MediaSessionConnector mediaSessionConnector;

    private static final int NOTIFY_ID = 1;

    static {
        playerCore = new ExoPlayer.Builder(App.get()).build();
        mediaSession = new MediaSessionCompat(App.get(), "main");
        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(playerCore);
    }

    public class MusicBinder extends Binder {
        public MediaPlayerService getService(){
            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return musicBinder;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        instance = this;
//        updateNotification(false);
        if (notification == null) {
            startForeground(NOTIFY_ID, new Notification());
            killService();
        } else
            startForeground(NOTIFY_ID, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    @Override
    public void onDestroy() {
        instance = null;
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        //TODO Stop service
    }

    public static void playSong(@NonNull Song currentSong, @NonNull List<Song> songList) {
        MediaItem mediaItem = MediaItem.fromUri(currentSong.getUri());
        playerCore.setMediaItem(mediaItem);
        playerCore.prepare();
        playerCore.play();
        updateNotification(true, currentSong);
    }

    @SuppressLint("RestrictedApi")
    private static void updateNotification(boolean foreground, @NonNull Song song) {
        // Initialize
        notificationBuilder = MediaPlayerUtils.createMediaPlayerNotificationBuilder();
        notificationManager = (NotificationManager) App.get().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(MediaPlayerActions.ACTION_OPEN_PLAYER);
        notifyIntent.setComponent(new ComponentName(App.get(), "com.example.ubiqplayer.UbiqPlayerActivity"));
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.get(), 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(R.drawable.ic_ubiqplayer_circle_small);

        // Propeties
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setColorized(true);

        // Specify actions
        Intent prev = new Intent(MediaPlayerActions.ACTION_PREV);
        Intent next = new Intent(MediaPlayerActions.ACTION_NEXT);
        Intent play = new Intent(MediaPlayerActions.ACTION_PLAY_PAUSE);
        prev.setClass(App.get(), MediaNotificationReceiver.class);
        next.setClass(App.get(), MediaNotificationReceiver.class);
        play.setClass(App.get(), MediaNotificationReceiver.class);
        PendingIntent pPrevious = PendingIntent.getBroadcast(App.get(), 0, prev, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pNext = PendingIntent.getBroadcast(App.get(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pPlay = PendingIntent.getBroadcast(App.get(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Bitmap thumb = song.getThumb();
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getTitle())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration()) // remove Android 10 notification seekbar
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, thumb).build());
        mediaSession.setActive(true); // TODO find a place for setActive(false)
        NotificationCompat.Action prevAction = new NotificationCompat.Action.Builder(R.drawable.ic_prev, "prev", pPrevious).build();
        NotificationCompat.Action nextAction = new NotificationCompat.Action.Builder(R.drawable.ic_next, "next", pNext).build();
        int playResId = playerCore.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play;
        NotificationCompat.Action playAction = new NotificationCompat.Action.Builder(playResId, "play", pPlay).build();

        notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0,1,2))
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtist())
                .addAction(prevAction)
                .addAction(playAction)
                .addAction(nextAction);
        if (thumb != null)
            notificationBuilder.setLargeIcon(thumb);
        else {
            Resources r = App.get().getResources();
            float px_200 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
            int pxInt_200 = Math.round(px_200);
            Drawable defaultThumb = AppCompatDrawableManager.get().getDrawable(App.get(), R.drawable.ic_audio_file);
            notificationBuilder.setLargeIcon(CommonUtils.getBitmap(defaultThumb, pxInt_200, pxInt_200));
        }
        // TODO Shuffle and loop



        // Removal intent
        Intent dismissIntent = new Intent(MediaPlayerActions.ACTION_NOTIFICATION_DISMISSED);
        dismissIntent.setClass(App.get(), MediaNotificationReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(App.get(), 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setDeleteIntent(pendingIntent1);
        notification = notificationBuilder.build();

        /////////////////////////////////////////////////////
        if (foreground)
            startInForeground();
        else
            notificationManager.notify(NOTIFY_ID, notification);
    }

    private static void startInForeground() {
        Intent intent = new Intent(App.get(), MediaPlayerService.class);
        ContextCompat.startForegroundService(App.get(), intent);
    }

    public static void killService() {
        if (instance != null) {
            instance.stopForeground(true);
            instance.stopSelf();
        }
        releasePlayer();
        instance = null;
    }

    private void pausePlayer() {
        playbackPosition = playerCore.getCurrentPosition();
        currentItem = playerCore.getCurrentMediaItem();
        playWhenReady = playerCore.getPlayWhenReady();
    }

    private static void releasePlayer() {
        if (mediaSession != null)
            mediaSession.release();
        playbackPosition = 0;
        currentItem = null;
        playWhenReady = false;
    }

    public static MediaPlayerService getInstance() {
        return instance;
    }

}
