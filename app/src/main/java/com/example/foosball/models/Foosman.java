package com.example.foosball.models;

import android.graphics.Point;
import android.graphics.Rect;

public class Foosman {

    private Point point = new Point(-1, -1);
    private Rect bounds;

    public Foosman() {
    }

    public void setPoint(int X, int Y) {
        this.point = new Point(X, Y);
    }

    public int getPointX() { return this.point.x; }

    public int getPointY() {
        return this.point.y;
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public int getWidth() {
        return bounds.width();
    }

    public int getHeight() {
        return bounds.height();
    }
}
