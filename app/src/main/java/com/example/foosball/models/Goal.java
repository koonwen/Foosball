package com.example.foosball.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Handles the logic for the goals.
 */
public class Goal {
    private int topY;
    private int bottomY;
    private int xPos;
    private final String name;
    private final Paint paint = new Paint();
    private int conceeded = 0;

    /**
     * Goal contructor. Goal name is the team name's goal
     *
     * @param name
     */
    public Goal(String name) {
        this.name = name;
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(20);
    }

    /**
     * Setter for goals location.
     *
     * @param x the x coordinate of the goal. Team A's x-coord goal should be 0 and Team B's
     *         x-coord at canvasWidth
     * @param top the maxY of the goal region
     * @param bottom the minY of the goal region
     */
    public void setGoalPoints(int x, int top, int bottom) {
        xPos = x;
        topY = top;
        bottomY = bottom;
    }

    /**
     * Function to check if the ball has been scored. Updates the class's goal conceeded count
     * and shows a popup of the goal scored.
     *
     * @param y
     */
    public boolean checkGoal(int y) {
        if (y < topY && y > bottomY) {
            scoreGoal();
            return true;
        }
        return false;
    }

    public void scoreGoal() {
        conceeded += 1;
    }

    /**
     * To be used by the gameboard to redraw the goal at every iteration.
     *
     * @param canvas
     */
    public void refresh(Canvas canvas) {
        canvas.drawLine(xPos, bottomY, xPos, topY, paint);
    }

    /**
     * Returns the number of goals conceeded
     *
     * @return
     */
    public int getConceeded() {
        return conceeded;
    }

}
