package com.example.foosball.models;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class that references all the individual Foosmen per team
 */

public class FoosmenTeam {
    private final ArrayList<Foosman> playerList = new ArrayList<>(5);
    private final ArrayList<Integer> relativePos = new ArrayList<>(5);
    private int yCord;

    /**
     * Sets the y coordinates of the foosmen team to the given ycord
     *
     * @param yCord Absolute y coordinates
     */
    public FoosmenTeam(int yCord) {
        this.yCord = yCord;
    }

    /**
     * Adds a Foosman to the team player list
     *
     * @param player New foosman player
     */
    public void addPlayer(Foosman player) {
        this.playerList.add(player);
    }

    /**
     * This method should only be called once to set up all the
     * relative positioning for the players in a team to the center point
     */
    public void fixRelativePos() {
        for (Foosman curFoosman : playerList) {
            relativePos.add(curFoosman.getPointY() - yCord);
        }
    }

    /**
     * Updates the yCord attribute after moving the foosmen team by the difference between
     * the old and new yCords
     *
     * @param yCord Integer, new absolute y coordinates.
     */
    public void setYCord(int yCord) {
        final int y = yCord - this.yCord;
        moveFoosmen(y);
        this.yCord = yCord;
    }

    /**
     * Returns the current y cord of the team
     *
     * @return Integer absolute y coordinates
     */

    public int getYCord() {
        return this.yCord;
    }

    /**
     * Set the coordinates of the players relative to the y position.
     * Might have bugs because of different screen sizes
     */
    public void setRelativePos() {
        Iterator<Foosman> iter1 = playerList.iterator();
        Iterator<Integer> iter2 = relativePos.iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            Foosman p = iter1.next();
            Integer i = iter2.next();
            p.setY(yCord + i);
        }
    }

    /**
     * Move all players in the Foosman Team
     *
     * @param y Relative y coordinates to move the team by
     */
    public void movePlayers(int y) {
        moveFoosmen(y);
        yCord += y;
    }

    /**
     * Move all individual foosmen.
     *
     * @param y Relative y displacement to move by.
     */
    private void moveFoosmen(int y) {
        for (Foosman f : playerList) {
            f.setY(f.getPointY() + y);
        }
    }
}
