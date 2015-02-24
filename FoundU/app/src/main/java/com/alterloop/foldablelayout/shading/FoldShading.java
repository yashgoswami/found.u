package com.alterloop.foldablelayout.shading;

/**
 * Created by Ankit on 07-12-2014.
 */
import android.graphics.Canvas;
import android.graphics.Rect;

public interface FoldShading {
    void onPreDraw(Canvas canvas, Rect bounds, float rotation, int gravity);

    void onPostDraw(Canvas canvas, Rect bounds, float rotation, int gravity);
}
