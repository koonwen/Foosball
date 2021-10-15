package com.example.foosball.models;

import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

public class Ball implements BoardItem {

    private Point point = new Point(-1, -1);
    private Rect bounds;
    private int ballRotation = 0;

    private boolean collisionDetected = false;
    private Point lastCollision;
    public Matrix mball = new Matrix();

    public int getBallRotation() {
        return ballRotation;
    }

    public void setBallRotation(int ballRotation) {
        this.ballRotation = ballRotation;
    }

    public void setCollisionDetected(boolean collisionDetected) {
        this.collisionDetected = collisionDetected;
    }

    public boolean isCollisionDetected() {
        return collisionDetected;
    }

    public Point getLastCollision() {
        return lastCollision;
    }

    public void setLastCollision(Point lastCollision) {
        this.lastCollision = lastCollision;
    }

    @Override
    public void setPoint(int X, int Y) {
        this.point = new Point(X, Y);
    }

    @Override
    public int getPointX() {
        return this.point.x;
    }

    @Override
    public int getPointY() {
        return this.point.y;
    }

    @Override
    public Rect getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    @Override
    public int getWidth() {
        return bounds.width();
    }

    @Override
    public int getHeight() {
        return bounds.height();
    }
}
