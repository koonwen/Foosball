package com.example.foosball.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public class Foosman implements BoardItem {

    private Point point = new Point(-1, -1);
    private Rect bounds;
    private String name;

    private Bitmap bm;

    public Foosman(String name, Bitmap bm) {
        this.name = name;
        this.bm = bm;
        this.bounds = new Rect(0, 0, bm.getWidth(), bm.getHeight());
    }


    public Bitmap getBm() {
        return bm;
    }

    public void setPoint(int X, int Y) {
        this.point = new Point(X, Y);
    }

    public void setY(int y) {this.point = new Point(point.x, y); }

    public int getPointX() {
        return this.point.x;
    }

    public int getPointY() {
        return this.point.y;
    }

    public int getWidth() {
        return bounds.width();
    }

    public int getHeight() {
        return bounds.height();
    }

    public void refresh(Canvas canvas) {
        canvas.drawBitmap(bm, point.x, point.y, null);
    }
}
