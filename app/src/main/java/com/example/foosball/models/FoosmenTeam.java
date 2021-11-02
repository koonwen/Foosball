package com.example.foosball.models;

import com.example.foosball.database.CoordsListener;
import com.example.foosball.database.Database;

import java.util.ArrayList;
import java.util.Iterator;

public class FoosmenTeam {
    private ArrayList<Foosman> playerList = new ArrayList<Foosman>(5);
    private ArrayList<Integer> relativePos = new ArrayList<Integer>(5);
    private int yCord;

    public FoosmenTeam(int yCord) {
        this.yCord = yCord;
    }

    public void addPlayer(Foosman player) {
        this.playerList.add(player);
    }

    /**
     * This method should only be called once to set up all the
     * relative positioning for the players in a team to the center point
     */
    public void fixRelativePos() {
        Iterator<Foosman> iterPlayer = playerList.iterator();
        while (iterPlayer.hasNext()) {
            Foosman curFoosman = iterPlayer.next();
            relativePos.add(curFoosman.getPointY()-yCord);
        }
    }

    public void setYCord(int yCord) {
        final int y = yCord - this.yCord;
        moveFoosmen(y);
        this.yCord = yCord;
    }

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
        for (Foosman f: playerList) {
            f.setY(f.getPointY() + y);
        }
    }
}
