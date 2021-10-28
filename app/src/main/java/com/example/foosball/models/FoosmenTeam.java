package com.example.foosball.models;

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
    public void getRelativePos() {
        Iterator<Foosman> iterPlayer = playerList.iterator();
        while (iterPlayer.hasNext()) {
            Foosman curFoosman = iterPlayer.next();
            relativePos.add(curFoosman.getPointY()-yCord);
        }
    }

    public void setyCord(int yCord) {
        this.yCord = yCord;
    }

    public int getyCord() {
        return yCord;
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
        yCord += y;
        Iterator<Foosman> itertor = playerList.iterator();
        while (itertor.hasNext()) {
            Foosman p = itertor.next();
            p.setY(p.getPointY() + y);
        }
    }
}
