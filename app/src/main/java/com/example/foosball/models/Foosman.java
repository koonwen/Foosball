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

    /**
     * Foosman contructor, takes identifier of the foosman and the bitmap
     * @param name
     * @param bm
     */
    public Foosman(String name, Bitmap bm) {
        this.name = name;
        this.bm = bm;
        this.bounds = new Rect(0, 0, bm.getWidth(), bm.getHeight());
    }

    public Foosman(String name) {
        this.name = name;
    }

    /**
     * Set the location of the foosman
     * @param X
     * @param Y
     */
    public void setPoint(int X, int Y) {
        this.point = new Point(X, Y);
    }

    /**
     * reset position Y of foosman
     * @param y
     */
    public void setY(int y) {this.point = new Point(point.x, y); }

    /**
     * Getter for x-coordinate
     * @return
     */
    public int getPointX() {
        return this.point.x;
    }

    /**
     * Getter for y-coordinate
     * @return
     */
    public int getPointY() {
        return this.point.y;
    }

    /**
     * Getter for foosman bitmap width
     * @return
     */
    public int getWidth() {
        return bounds.width();
    }

    /**
     * Getter for foosman bitmap height
     * @return
     */
    public int getHeight() {
        return bounds.height();
    }

    /**
     * Refresh function to redraw foosman at every frame update
     * @param canvas
     */
    public void refresh(Canvas canvas) {
        canvas.drawBitmap(bm, point.x, point.y, null);
    }
}
