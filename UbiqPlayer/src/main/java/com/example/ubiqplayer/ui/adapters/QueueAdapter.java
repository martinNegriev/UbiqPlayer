package com.example.ubiqplayer.ui.adapters;

import android.content.Context;

import com.example.ubiqplayer.R;


public class QueueAdapter extends HomeAdapter{

    public QueueAdapter(Context context) {
        super(context, null);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.queue_item;
    }
}
