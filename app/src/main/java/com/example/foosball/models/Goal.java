package com.example.foosball.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;

public class Goal {
    private int topY;
    private int bottomY;
    private int xPos;
    private String name;
    private Paint paint = new Paint();
    private int conceeded = 0;

    public Goal(String name) {
        this.name = name;
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(20);
    }

    public void setGoalPoints(int x, int top, int bottom) {
        xPos = x;
        topY = top;
        bottomY = bottom;
    }

    public boolean checkGoal(int y, View view) {
        if (y < topY && y > bottomY) {
            conceeded += 1;
            Snackbar.make(view, "GOAL!!! " + name + " conceeded " + conceeded + " goals", BaseTransientBottomBar.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    public void refresh(Canvas canvas) {
        canvas.drawLine(xPos, bottomY, xPos, topY, paint);
    }

    public int getConceeded() {return conceeded;}

}
