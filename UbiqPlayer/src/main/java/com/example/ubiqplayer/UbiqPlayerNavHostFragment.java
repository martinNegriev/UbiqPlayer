package com.example.ubiqplayer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

public class UbiqPlayerNavHostFragment extends NavHostFragment {
    private boolean attached = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        attached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        attached = false;
    }

    public boolean isAttached() {
        return attached;
    }
}
