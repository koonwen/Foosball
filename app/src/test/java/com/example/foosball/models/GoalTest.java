package com.example.foosball.models;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GoalTest {

    Goal g = new Goal("TestGoal");

    @Test
    public void checkGoal() {
        g.setGoalPoints(5, 10, 2);
        assertTrue(g.checkGoal(3));
        assertFalse(g.checkGoal(1));
        assertFalse(g.checkGoal(11));
    }

    @Test
    public void getConceeded() {
        assertEquals(0, g.getConceeded());
    }

    @Test
    public void scoreGoal() {
        g.scoreGoal();
        assertEquals(1, g.getConceeded());
    }

}