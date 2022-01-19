package com.example.ubiqplayer;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.mediaplayer.UbiqPlayerLogic;
import com.example.ubiqplayer.ui.customviews.MusicBottomSheet;
import com.example.ubiqplayer.ui.models.Song;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ubiqplayer.databinding.ActivityUbiqPlayerBinding;

import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UbiqPlayerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1111;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUbiqPlayerBinding binding;
    private MusicBottomSheet musicBottomSheet;
    private UbiqPlayerLogic playerLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (needsPermissions())
            return;

        initComponents();
    }

    private void initComponents() {
        binding = ActivityUbiqPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarUbiqPlayer.toolbar);

        musicBottomSheet = getMusicBottomSheet();

        if (MediaPlayerService.isPlaying()) { // If theme is changed
            binding.appBarUbiqPlayer.fab.setVisibility(View.VISIBLE);
            musicBottomSheet.refreshUI();
        }
        binding.appBarUbiqPlayer.fab.setOnClickListener(view -> musicBottomSheet.toggleBottomSheet());

        playerLogic = new UbiqPlayerLogic(UbiqPlayerActivity.this);
        playerLogic.tryRegisterReceiver();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_playlists, R.id.nav_favorites, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_ubiq_player);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_ubiq_player);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initComponents();
            else
                finish();
        }
    }

    private boolean needsPermissions() {
        if (!hasReadPermissions() || !hasWritePermissions()) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            return true;
        }
        return false;
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public void startPlayback(@NonNull Song playedSong, @NonNull List<Song> songList) {
        binding.appBarUbiqPlayer.fab.setVisibility(View.VISIBLE);
        MediaPlayerService.playSong(playedSong, songList);
    }

    @NonNull
    public MusicBottomSheet getMusicBottomSheet() {
        if (musicBottomSheet == null) {
            LinearLayout bottomSheet = binding.appBarUbiqPlayer.musicBottomSheet.bottomSheetContent;
            BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            musicBottomSheet = new MusicBottomSheet(bottomSheet, bottomSheetBehavior);
        }
        return musicBottomSheet;
    }

    public View getFAB() {
        return binding.appBarUbiqPlayer.fab;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerLogic.tryUnregisterReceiver();
    }
}