package com.example.foosball.models;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class handling the graphics for the game board's background
 */
public class Background {

    private List<Point> starField = null;
    private int starAlpha = 80;
    private int starFade = 2;
    private static final int NUM_OF_STARS = 36;
    private Paint p = new Paint();

    /**
     * Resets star field to null on every new game
     */
    synchronized public void resetStarField() {
        starField = null;
    }

    /**
     * initializing sequence for the star field
     * @param maxX canvas maxX
     * @param maxY canvas maxY
     */
    synchronized private void initializeStars(int maxX, int maxY) {
        starField = new ArrayList<Point>();
        for (int i = 0; i < NUM_OF_STARS; i++) {
            Random r = new Random();
            int x = r.nextInt(maxX - 5 + 1) + 5;
            int y = r.nextInt(maxY - 5 + 1) + 5;
            starField.add(new Point(x, y));
        }
    }

    /**
     * refresh function to be called every frame to produce blinking effect
     * @param canvas
     * @param maxX
     * @param maxY
     */
    public void refresh(Canvas canvas, int maxX, int maxY) {
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, maxX, maxY, p);
        if (starField == null) {
            initializeStars(maxX, maxY);
        }
        p.setColor(Color.CYAN);
        p.setAlpha(starAlpha += starFade);
        if (starAlpha >= 252 || starAlpha <= 80) starFade = starFade * -1;
        p.setStrokeWidth(5);
        for (int i = 0; i < NUM_OF_STARS; i++) {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
        }
    }
}
