package com.example.ubiqplayer.ui.queue;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubiqplayer.App;
import com.example.ubiqplayer.BaseFragment;
import com.example.ubiqplayer.R;
import com.example.ubiqplayer.mediaplayer.MediaPlayerService;
import com.example.ubiqplayer.ui.adapters.QueueAdapter;
import com.example.ubiqplayer.ui.interfaces.ISongAddedListener;
import com.example.ubiqplayer.ui.viewmodels.QueueViewModel;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class QueueFragment extends BaseFragment implements ISongAddedListener {
    public static final String QUEUE_EXTRA = "QUEUE_EXTRA";

    private QueueViewModel songViewModel;
    private RecyclerView recyclerView;
    private QueueAdapter songAdapter;
    private View emptyLayout;
    private View recyclerViewlayout;
    private View addSongsButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songViewModel = new ViewModelProvider(this).get(QueueViewModel.class);

        // Start observing
        songViewModel.getSongsData().observe(this, songs -> {
            if (songs == null || songs.isEmpty()) {
                emptyLayout.setVisibility(View.VISIBLE);
                recyclerViewlayout.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.GONE);
                recyclerViewlayout.setVisibility(View.VISIBLE);
            }
            songAdapter.updateData(songs);
        });
    }

    private void initRecyclerView() {
        songAdapter = new QueueAdapter(getContext());
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary, getContext().getTheme()))
                        .addSwipeLeftActionIcon(R.drawable.ic_remove_24dp)
                        .addSwipeRightBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary, getContext().getTheme()))
                        .addSwipeRightActionIcon(R.drawable.ic_remove_24dp)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int curInd = viewHolder.getLayoutPosition();
                int newInd = target.getLayoutPosition();
                if (curInd < 0 || newInd < 0)
                    return false;
                songAdapter.notifyItemMoved(curInd, newInd);
                MediaPlayerService.moveMediaItem(curInd, newInd);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getLayoutPosition();
                if (pos < 0)
                    return;
                songAdapter.notifyItemRemoved(pos);
                MediaPlayerService.removeMediaItem(pos);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_queue, container, false);
        // Init recyclerView, view model, adapter
        recyclerViewlayout = root.findViewById(R.id.recycler_view_layout);
        recyclerView = root.findViewById(R.id.queue_recycler_view);
        addSongsButton = root.findViewById(R.id.add_songs_lower_button);
        addSongsButton.setOnClickListener(v -> openPickSongsFragment(this, "", true));
        recyclerView.setItemAnimator(null);
        emptyLayout = root.findViewById(R.id.no_songs_in_queue_layout);
        initRecyclerView();
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        songViewModel.loadQueuedSongs();
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return songAdapter;
    }

    @Override
    protected boolean isNavChildFragment() {
        return true;
    }

    @NonNull
    @Override
    public String getLocationName() {
        return App.get().getResources().getString(R.string.menu_queue);
    }

    @Override
    public void reloadIfNeeded() {
        songViewModel.loadQueuedSongs();
    }

    @Override
    public void onSongAdded() {
        reloadIfNeeded();
    }
}
