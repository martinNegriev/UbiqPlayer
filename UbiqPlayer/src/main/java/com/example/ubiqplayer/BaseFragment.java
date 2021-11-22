package com.example.ubiqplayer;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    public UbiqPlayerActivity getUbiqPlayerActivity() {
        return (UbiqPlayerActivity) getActivity();
    }
}
