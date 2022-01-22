package com.example.ubiqplayer.ui.home;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.BaseFragment;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.ui.adapters.HomeAdapter;
import com.example.ubiqplayer.ui.interfaces.ISongClickListener;
import com.example.ubiqplayer.ui.models.Song;
import com.example.ubiqplayer.ui.sorting.SortExtras;
import com.example.ubiqplayer.ui.sorting.SortLocation;
import com.example.ubiqplayer.ui.sorting.SortOption;
import com.example.ubiqplayer.ui.viewmodels.HomeViewModel;
import com.example.ubiqplayer.utils.CommonUtils;

import java.util.List;

public class HomeFragment extends BaseFragment implements ISongClickListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private View sortLayout;
    private TextView sortBy;
    private ImageView sortDirection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Start observing
        observe();
    }

    protected void observe() {
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
        sortLayout = root.findViewById(R.id.sort_layout);
        sortBy = root.findViewById(R.id.sorted_by);
        sortDirection = root.findViewById(R.id.sort_direction);
        sortLayout.setOnClickListener(v -> openSortDialog(SortLocation.Song));
        recyclerView.setItemAnimator(null);
        initRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        applySortAndLoad();
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
    }

    @Override
    protected boolean isNavChildFragment() {
        return true;
    }

    @NonNull
    @Override
    public String getLocationName() {
        return App.get().getResources().getString(R.string.title_home);
    }

    @Override
    protected void applySortAndLoad() {
        SortOption option = SortOption.getByName(CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getString(SortExtras.SORT_EXTRA_SONGS, SortOption.Title.name()));
        boolean reversed = CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getBoolean(SortExtras.SORT_EXTRA_SONGS_REVERSED, false);
        App.HANDLER.post(() -> {
            sortBy.setText(option.getName());
            sortDirection.setImageDrawable(ResourcesCompat.getDrawable(App.get().getResources(), reversed ? R.drawable.ic_desc : R.drawable.ic_asc, getContext().getTheme()));
            sortDirection.setColorFilter(getContext().getResources().getColor(R.color.textColor, getContext().getTheme()), PorterDuff.Mode.SRC_IN);
        });
        homeViewModel.loadSongsData(option, reversed);
    }
}