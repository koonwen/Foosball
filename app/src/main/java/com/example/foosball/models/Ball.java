package com.example.foosball.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * The on-screen ball object
 * public variable: Matrix mball
 */
public class Ball implements BoardItem {

    private Point point = new Point(-1, -1);
    private int ballRotation = 0;
    private boolean collisionDetected = false;
    private Point lastCollision;

    private Rect bounds;
    private Bitmap bm;

    private Matrix mball = new Matrix();

    public Ball(Bitmap bm) {
        this.bm = bm;
        this.bounds = new Rect(0, 0, bm.getWidth(), bm.getHeight());
    }

    public Matrix getMball() {
        return mball;
    }

    /**
     * Getter for ball rotation
     *
     * @return ballRotation in degrees
     */
    public int getBallRotation() {
        return ballRotation;
    }

    /**
     * Setter for ball rotation
     *
     * @param ballRotation
     */
    public void setBallRotation(int ballRotation) {
        this.ballRotation = ballRotation;
    }

    /**
     * Setter for whether collision has been detected or not
     *
     * @param collisionDetected
     */
    public void setCollisionDetected(boolean collisionDetected) {
        this.collisionDetected = collisionDetected;
    }

    /**
     * Getter for collision status
     *
     * @return boolean
     */
    public boolean isCollisionDetected() {
        return collisionDetected;
    }

    /**
     * Getter for last Point where collision was detected
     *
     * @return Point object
     */
    public Point getLastCollision() {
        return lastCollision;
    }

    /**
     * Setter for the most recent collision point
     *
     * @param lastCollision
     */
    public void setLastCollision(Point lastCollision) {
        this.lastCollision = lastCollision;
    }

    /**
     * Setter for ball position point
     *
     * @param X
     * @param Y
     */
    @Override
    public void setPoint(int X, int Y) {
        this.point = new Point(X, Y);
    }

    /**
     * Getter for x-coordinate of the ball position
     *
     * @return x-coordinate
     */
    @Override
    public int getPointX() {
        return this.point.x;
    }

    /**
     * Getter for the y-coordinate of the ball position
     *
     * @return y-coordinate
     */
    @Override
    public int getPointY() {
        return this.point.y;
    }

    /**
     * Getter for the bounds of the ball object
     *
     * @return bounds of the ball
     */
//    @Override
//    public Rect getBounds() {
//        return bounds;
//    }
//
//    /**
//     * Setter for the bounds of the ball object
//     * @param bounds
//     */
//    @Override
//    public void setBounds(Rect bounds) {
//        this.bounds = bounds;
//    }
//
//    /**
//     * Getter for the width of the ball object bounds
//     * @return ball object bound width
//     */
    @Override
    public int getWidth() {
        return bounds.width();
    }

    /**
     * Getter for the height of the ball object bounds
     *
     * @return ball object bound height
     */
    @Override
    public int getHeight() {
        return bounds.height();
    }

    public void refresh(Canvas canvas) {
        if (point.x > 0) {
            mball.reset();
            mball.postTranslate((float) (point.x), (float) (point.y));
            mball.postRotate(ballRotation, (float) (point.x + bounds.width() / 2.0), (float) (point.y + bounds.width() / 2.0));
            ballRotation = (ballRotation + 5) % 360;
        }
        canvas.drawBitmap(bm, mball, null);
    }

    public boolean goalScored(Goal goal1, Goal goal2) {
        if (point.y < goal1.getTop() && point.y > goal1.getBottom() &&
            point.x < goal1.getLeft() && point.x > goal1.getRight() ||
            point.y < goal2.getTop() && point.y > goal2.getBottom() &&
            point.x < goal2.getLeft() && point.x > goal2.getRight()) {
            return true;
        } return false;
    }
}
