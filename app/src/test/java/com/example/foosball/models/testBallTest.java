package com.example.foosball.models;

import junit.framework.TestCase;

import org.junit.Test;

class testGoalTest extends TestCase {

    @Test
    public void testBallAttributes() {
        Goal g = new Goal("testGoal");
        g.setGoalPoints(0, 10, 20);
        assertEquals(g.getConceeded(), 0);
    }

}