package com.example.foosball.database;

/**
 * Classes implementing this interface can be used to receive events about any updates to the
 * ball coordinates and velocity.
 */

public interface GameDataListener extends DatabaseListener {
    /**
     * This method will be called in the event that accessing the balls coords is successful.
     *
     * @param x x coordinates of the ball.
     * @param y y coordinates of the ball.
     * @param vx x velocity of the ball.
     * @param vy y-velocity of the ball.
     * @param fya y-position of the team A foosmen.
     * @param scoreA Score for team A.
     * @param scoreB Score for team B.
     */
    void onSuccess(int x, int y, int vx, int vy, int fya, int fyb, int scoreA, int scoreB);
}
