package com.example.foosball.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class Goal {
    private Point point = new Point(-1, -1);
    private Rect bounds;
    private String name;
    private Bitmap bm;

    public Goal(String name, Bitmap bm) {
        this.name = name;
        this.bm = bm;
        this.bounds = new Rect(0, 0, bm.getWidth(), bm.getHeight());
    }

    public void refresh(Canvas canvas) {
        canvas.drawBitmap(bm, point.x, point.y, null);
    }

    public void setPoint(int x, int y) {
        point.x = x;
        point.y = y;
    }

    public int getTop(){
        return bounds.height()/2 + point.y;
    }

    public int getBottom(){
        return point.y - bounds.height()/2;
    }

    public int getRight(){
        return bounds.width()/2 + point.x;
    }

    public int getLeft(){
        return point.x - bounds.width()/2;
    }

}
