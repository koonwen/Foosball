package com.example.foosball.models;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class BallTest {

    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    Bitmap bm = Bitmap.createBitmap(10, 10 , conf);
    Ball b = new Ball(bm);

    @Test
    public void getMball() {
        assertEquals(new Matrix(), b.getMball());
    }

    @Test
    public void getBallRotation() {
        assertEquals(0, b.getBallRotation());
    }

    @Test
    public void setBallRotation() {
        b.setBallRotation(10);
        assertEquals(10, b.getBallRotation());
    }

    @Test
    public void setCollisionDetected() {
        b.setCollisionDetected(true);
        assertTrue(b.isCollisionDetected());
    }

    @Test
    public void isCollisionDetected() {
        assertFalse(b.isCollisionDetected());
    }

    @Test
    public void getLastCollision() {
        assertNull(b.getLastCollision());
    }

    @Test
    public void setLastCollision() {
        b.setLastCollision(new Point(1,1));
        assertEquals(new Point(1,1), b.getLastCollision());
    }

    @Test
    public void setPoint() {
        b.setPoint(3, 3);
        assertEquals(3, b.getPointX());
        assertEquals(3, b.getPointY());
    }

    @Test
    public void getPointX() {
        assertEquals(-1, b.getPointX());
    }

    @Test
    public void getPointY() {
        assertEquals(-1, b.getPointY());
    }

    @Test
    public void getWidth() {
        assertEquals(10, b.getWidth());
    }

    @Test
    public void getHeight() {
        assertEquals(10, b.getHeight());
    }
}