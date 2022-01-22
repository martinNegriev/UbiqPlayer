package com.example.ubiqplayer.ui.playlists;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.BaseFragment;
import com.example.ubiqplayer.R;

import com.example.ubiqplayer.persistence.Playlist;
import com.example.ubiqplayer.persistence.PlaylistWithSongs;
import com.example.ubiqplayer.persistence.SongDatabase;
import com.example.ubiqplayer.ui.adapters.PlaylistsAdapter;
import com.example.ubiqplayer.ui.helper.ResultTask;
import com.example.ubiqplayer.ui.interfaces.IPlaylistClickListener;
import com.example.ubiqplayer.ui.sorting.SortExtras;
import com.example.ubiqplayer.ui.sorting.SortLocation;
import com.example.ubiqplayer.ui.sorting.SortOption;
import com.example.ubiqplayer.ui.viewmodels.PlaylistViewModel;
import com.example.ubiqplayer.utils.CommonUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Collections;


public class PlaylistsFragment extends BaseFragment implements IPlaylistClickListener {

    public static final String PLAYLIST_NAME_EXTRA = "PLAYLIST_NAME_EXTRA";

    private PlaylistViewModel playlistViewModel;
    private RecyclerView recyclerView;
    private View emptyLayout;
    private PlaylistsAdapter playlistsAdapter;
    private ImageView addPlaylistButton;
    private View recyclerViewLayout;
    private View sortLayout;
    private TextView sortBy;
    private ImageView sortDirection;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlistViewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);

        // Start observing
        playlistViewModel.getPlaylistsData().observe(this, playlists -> {
            if (playlists == null || playlists.isEmpty()) {
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerViewLayout.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.GONE);
                recyclerViewLayout.setVisibility(View.VISIBLE);
            }
            playlistsAdapter.updateData(playlists);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlists, container, false);
        // Init recyclerView, view model, adapter
        recyclerView = root.findViewById(R.id.playlists_recycler_view);
        recyclerView.setItemAnimator(null);
        emptyLayout = root.findViewById(R.id.no_playlists_layout);
        addPlaylistButton = root.findViewById(R.id.add_playlist_button);
        recyclerViewLayout = root.findViewById(R.id.recycler_view_layout);
        sortLayout = root.findViewById(R.id.sort_layout);
        sortBy = sortLayout.findViewById(R.id.sorted_by);
        sortDirection = sortLayout.findViewById(R.id.sort_direction);
        sortLayout.setOnClickListener(v -> openSortDialog(SortLocation.Playlist));
        addPlaylistButton.setOnClickListener(v -> addPlaylist());
        initRecyclerView();
        setHasOptionsMenu(true);
        return root;
    }

    private void addPlaylist() {
        final EditText editText= new EditText(this.getContext());
        AlertDialog dialog = new MaterialAlertDialogBuilder(this.getContext(), R.style.MultiChoiceAlertDialog)
                .setTitle(App.get().getResources().getString(R.string.new_playlist))
                .setMessage(App.get().getResources().getString(R.string.enter_playlist_name))
                .setView(editText)
                .setPositiveButton(App.get().getResources().getString(R.string.ok_button), (dialogInterface, i) -> {
                    String editTextInput = editText.getText().toString();
                    new Thread(() -> {
                        Playlist existing = SongDatabase.getInstance().songDao().getPlaylistByName(editTextInput);
                        if (existing != null) {
                            App.HANDLER.post(() -> Toast.makeText(App.get(), App.get().getResources().getString(R.string.playlist_already_exists_msg), Toast.LENGTH_LONG).show());
                            return;
                        }
                        Playlist playlist = new Playlist();
                        playlist.playlistName = editTextInput;
                        SongDatabase.getInstance().songDao().insertPlaylists(Collections.singletonList(playlist));
                        applySortAndLoad();
                    }).start();
                })
                .setNegativeButton(App.get().getResources().getString(R.string.cancel_button), null)
                .create();
        dialog.show();
        if (editText.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
            p.setMargins(CommonUtils.dpToPx(20), p.topMargin, CommonUtils.dpToPx(20), p.bottomMargin);
            editText.setLayoutParams(p);
            editText.requestLayout();
        }
    }

    private void initRecyclerView() {
        playlistsAdapter = new PlaylistsAdapter(this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(playlistsAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        applySortAndLoad();
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return playlistsAdapter;
    }

    @Override
    public void onClick(@NonNull PlaylistWithSongs clicked) {
        FragmentManager mgr = getUbiqPlayerActivity().getSupportFragmentManager();
        FragmentTransaction ft = mgr.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in, R.anim.slide_out);
        View contentLayout = getActivity().findViewById(R.id.content_layout);
        contentLayout.setVisibility(View.VISIBLE);
        PlaylistWithSongsFragment playlistWithSongsFragment = new PlaylistWithSongsFragment();
        Bundle args = new Bundle();
        args.putString(PLAYLIST_NAME_EXTRA, clicked.playlist.playlistName);
        playlistWithSongsFragment.setArguments(args);
        ft.addToBackStack(null).replace(R.id.content_layout, playlistWithSongsFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.playlist_action_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_playlist) {
            addPlaylist();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isNavChildFragment() {
        return true;
    }

    @NonNull
    @Override
    public String getLocationName() {
        return App.get().getResources().getString(R.string.menu_playlists);
    }

    public void reloadData() {
        applySortAndLoad();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void removePlaylist(@NonNull PlaylistWithSongs removed) {
        new ResultTask<Integer>() {

            @Override
            protected Integer doInBackground() {
                return SongDatabase.getInstance().songDao().deletePlaylist(removed.playlist);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (integer <= 0)
                    return;
                applySortAndLoad();
            }
        }.start();
    }

    @Override
    protected void applySortAndLoad() {
        SortOption option = SortOption.getByName(CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getString(SortExtras.SORT_EXTRA_PLAYLISTS, SortOption.Name.name()));
        boolean reversed = CommonUtils.getSharedPrefs(SortExtras.SORT_PREFS_NAME).getBoolean(SortExtras.SORT_EXTRA_PLAYLISTS_REVERSED, false);
        App.HANDLER.post(() -> {
            sortBy.setText(option.getName());
            sortDirection.setImageDrawable(ResourcesCompat.getDrawable(App.get().getResources(), reversed ? R.drawable.ic_desc : R.drawable.ic_asc, getContext().getTheme()));
            sortDirection.setColorFilter(getContext().getResources().getColor(R.color.textColor, getContext().getTheme()), PorterDuff.Mode.SRC_IN);
        });
        playlistViewModel.loadPlaylistsData(option, reversed);
    }
}