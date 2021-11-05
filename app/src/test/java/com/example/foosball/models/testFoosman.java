package com.example.foosball.models;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import org.junit.Test;

public class testFoosman {

    private Foosman f = new Foosman("toto");

    @Test
    public void testFoosmanAttributes() {
        assertEquals(f.getPointY(), 0);
        assertEquals(f.getPointX(), 0);
    }
}
