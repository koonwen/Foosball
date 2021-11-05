package com.example.foosball.models;

import static org.junit.Assert.*;

import android.graphics.Bitmap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FoosmanTest {

    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    Bitmap bm = Bitmap.createBitmap(10,10, conf);

    @Test
    public void setPoint() {
        Foosman f = new Foosman("toto");
        f.setPoint(1, 1);
        assertEquals(1, f.getPointY());
    }

    @Test
    public void setY() {
        Foosman f = new Foosman("tata");
        f.setY(10);
        assertEquals(10, f.getPointY());
    }

    @Test
    public void getPointX() {
        Foosman f = new Foosman("titi");
        assertEquals(-1, f.getPointX());
    }

    @Test
    public void getPointY() {
        Foosman f = new Foosman("titi");
        assertEquals(-1, f.getPointY());
    }

    @Test
    public void getWidth() {
        Foosman f = new Foosman("toto", bm);
        assertEquals(10, f.getWidth());
    }

    @Test
    public void getHeight() {
        Foosman f = new Foosman("toto", bm);
        assertEquals(10, f.getHeight());
    }
}