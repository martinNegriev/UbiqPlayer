package com.example.ubiqplayer.ui.helper;

import android.os.AsyncTask;

import com.example.ubiqplayer.utils.CommonUtils;

public abstract class ResultTask<T> extends AsyncTask<Void, Void, T> {


    protected ResultTask() {
    }


    @Override
    protected final T doInBackground(Void... params) {
        String backup = Thread.currentThread().getName();
        Thread.currentThread().setName("ResultTask " + hashCode());
        try {
            return doInBackground();
        } catch (Throwable t) {
            return null;
        } finally {
            Thread.currentThread().setName(backup);
        }
    }

    protected abstract T doInBackground();

    public void start() {
        executeOnExecutor(CommonUtils.UNBOUNDED_EXECUTOR);
    }
}

