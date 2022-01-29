package com.example.ubiqplayer.ui.mostplayed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.BaseFragment;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.ui.adapters.HomeAdapter;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.viewmodels.MostPlayedViewModel;

import java.util.List;

public class MostPlayedFragment extends BaseFragment implements ISongClickListener {

    private MostPlayedViewModel mostPlayedViewModel;
    private RecyclerView recyclerView;
    private HomeAdapter mostPlayedAdapter;
    private View emptyLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mostPlayedViewModel = new ViewModelProvider(this).get(MostPlayedViewModel.class);

        // Start observing
        observe();
    }

    protected void observe() {
        mostPlayedViewModel.getMostPlayedData().observe(this, mostPlayed -> {
            if (mostPlayed == null || mostPlayed.isEmpty()) {
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            mostPlayedAdapter.updateData(mostPlayed);
        });
    }

    private void initRecyclerView() {
        mostPlayedAdapter = new HomeAdapter(getContext(), this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(mostPlayedAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_most_played, container, false);
        // Init recyclerView, view model, adapter
        recyclerView = root.findViewById(R.id.most_played_recycler_view);
        emptyLayout = root.findViewById(R.id.no_most_played_layout);
        recyclerView.setItemAnimator(null);
        initRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mostPlayedViewModel.loadMostPlayedData();
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return mostPlayedAdapter;
    }

    @Override
    public void onClick(@NonNull Song playerSong) {
        List<Song> songs = mostPlayedViewModel.getMostPlayedData().getValue();
        if (songs == null)
            return;
        getUbiqPlayerActivity().startPlayback(playerSong, songs);
    }

    @Override
    protected boolean isNavChildFragment() {
        return true;
    }

    @NonNull
    @Override
    public String getLocationName() {
        return App.get().getResources().getString(R.string.title_most_played);
    }

    @Override
    public void reloadIfNeeded() {
        mostPlayedViewModel.loadMostPlayedData();
    }
}
