package com.example.ubiqplayer.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.BaseFragment;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.ui.adapters.HomeAdapter;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.viewmodels.HomeViewModel;

import java.util.List;

public class HomeFragment extends BaseFragment implements ISongClickListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Start observing
        homeViewModel.getSongsData().observe(this, songs -> homeAdapter.updateData(songs));
    }

    private void initRecyclerView() {
        homeAdapter = new HomeAdapter(getContext(), this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(homeAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // Init recyclerView, view model, adapter
        recyclerView = root.findViewById(R.id.home_recycler_view);
        initRecyclerView();
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.loadSongsData();
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return homeAdapter;
    }

    @Override
    public void onClick(@NonNull Song playerSong) {
        List<Song> songs = homeViewModel.getSongsData().getValue();
        if (songs == null)
            return;
        getUbiqPlayerActivity().startPlayback(playerSong, songs);
        notifyAdapterDataSetChanged();
    }
}