package com.example.foosball.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class testBall {

    Ball b = new Ball();

    @Test
    public void testBallAttributes() {
        assertEquals(b.getBallRotation(), 0);
        assertEquals(b.isCollisionDetected(), false);
        assertEquals(b.getPointX(), 0);
        assertEquals(b.getPointY(), 0);
    }

}