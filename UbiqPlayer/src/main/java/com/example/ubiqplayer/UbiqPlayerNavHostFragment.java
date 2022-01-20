package com.example.ubiqplayer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ubiqplayer.mediaplayer.UbiqPlayerLogic;

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

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        UbiqPlayerActivity act = ((UbiqPlayerActivity)getActivity());
        UbiqPlayerLogic playerLogic = act.getPlayerLogic();
        if (playerLogic == null)
            return;
        Fragment curFrag = playerLogic.getCurrentFragment();
        if (!(curFrag instanceof BaseFragment))
            return;
        if (((BaseFragment) curFrag).isNavChildFragment())
            return;
        act.getSupportFragmentManager().popBackStackImmediate();
    }

    public boolean isAttached() {
        return attached;
    }
}
