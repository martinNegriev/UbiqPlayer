package com.example.ubiqplayer.ui.favorites;

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
import com.example.ubiqplayer.ui.adapters.FavoritesAdapter;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.viewmodels.FavoritesViewModel;

import java.util.List;

public class FavoritesFragment extends BaseFragment implements ISongClickListener {

    private FavoritesViewModel favsViewModel;
    private RecyclerView recyclerView;
    private FavoritesAdapter favsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favsViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        // Start observing
        favsViewModel.getFavsData().observe(this, songs -> favsAdapter.updateData(songs));
    }

    private void initRecyclerView() {
        favsAdapter = new FavoritesAdapter(getContext(), this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(favsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        // Init recyclerView, view model, adapter
        recyclerView = root.findViewById(R.id.favorites_recycler_view);
        initRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        favsViewModel.loadFavsData();
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return favsAdapter;
    }


    @Override
    public void onClick(@NonNull Song playerSong) {
        List<Song> songs = favsViewModel.getFavsData().getValue();
        if (songs == null)
            return;
        getUbiqPlayerActivity().startPlayback(playerSong, songs);
    }
}