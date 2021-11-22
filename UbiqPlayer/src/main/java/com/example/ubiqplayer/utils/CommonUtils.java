package com.example.ubiqplayer.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommonUtils {

    public static final Executor UNBOUNDED_EXECUTOR = Executors.newCachedThreadPool();

    public static float dpToPxFloat(float dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dp * scale);
    }

    public static int dpToPx(float dp) {
        // Is for positive values as written, negative should add -0.5f, see TypedValue.complexToDimensionPixelSize().
        // It's not known that negative values are ever used.
        return (int) (dpToPxFloat(dp) + 0.5f);
    }

    public static Bitmap getBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }
}
