package com.example.ubiqplayer;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseFragment extends Fragment {

    public UbiqPlayerActivity getUbiqPlayerActivity() {
        return (UbiqPlayerActivity) getActivity();
    }

    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter() {
        return null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterDataSetChanged() {
        RecyclerView.Adapter<RecyclerView.ViewHolder> adapter = getAdapter();
        if (adapter == null)
            return;
        adapter.notifyDataSetChanged();
    }

    protected boolean isNavChildFragment() {
        return false;
    }

    @NonNull
    public String getLocationName() {
        return "";
    }
}
