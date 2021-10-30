package com.example.foosball.database;

/**
 * Classes implementing this interface can be used to receive events about any updates to the
 * ball coordinates and velocity.
 */

public interface BallCoordsListener extends DatabaseListener {
    /**
     * This method will be called in the event that accessing the balls coords is successful.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param vx x velocity
     * @param vy y velocity
     */
    void onSuccess(int x, int y, int vx, int vy);
}
