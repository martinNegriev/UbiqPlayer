package com.example.ubiqplayer.ui.playlists;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ubiqplayer.BaseFragment;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.mediaplayer.UbiqPlayerLogic;
import com.example.ubiqplayer.ui.home.HomeFragment;

public class PlaylistWithSongsFragment extends HomeFragment {

    private View emptyLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null)
            return;
        String playlistName = args.getString(PlaylistsFragment.PLAYLIST_NAME_EXTRA);
        if (TextUtils.isEmpty(playlistName))
            return;
        getUbiqPlayerActivity().getSupportActionBar().setTitle(playlistName);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        emptyLayout = v.findViewById(R.id.no_songs_in_playlist_layout);
        return v;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_playlist_with_songs;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UbiqPlayerLogic playerLogic = getUbiqPlayerActivity().getPlayerLogic();
        if (playerLogic != null) {
            Fragment curFrag = playerLogic.getCurrentFragment();
            if (curFrag instanceof BaseFragment && !TextUtils.isEmpty(((BaseFragment)curFrag).getLocationName())) {
                getUbiqPlayerActivity().getSupportActionBar().setTitle(((BaseFragment)curFrag).getLocationName());
            }
        }
        getActivity().findViewById(R.id.content_layout).setVisibility(View.GONE);
    }

    @Override
    protected boolean isNavChildFragment() {
        return false;
    }

    @Override
    protected void observe() {
        // Start observing
        homeViewModel.getSongsData().observe(this, songs -> {
            if (songs == null || songs.isEmpty()) {
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            homeAdapter.updateData(songs);
        });
    }
}
