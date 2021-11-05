package com.example.foosball.models;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FoosmenTeamTest {

    FoosmenTeam ft = new FoosmenTeam(0);
    Foosman toto = new Foosman("toto");
    Foosman tata = new Foosman("tata");

    @Test
    public void addPlayer() {
        ft.addPlayer(toto);
        ft.addPlayer(tata);
        assertTrue(true);
    }

    @Test
    public void fixRelativePos() {
        toto.setY(3);
        tata.setY(10);
        ft.fixRelativePos();
        ft.setRelativePos();
        assertEquals(3, toto.getPointY());
        assertEquals(10, tata.getPointY());
    }

    @Test
    public void setYCord() {
        ft.addPlayer(toto);
        ft.addPlayer(tata);
        ft.setYCord(5);
        assertEquals(4, toto.getPointY());
        assertEquals(4, tata.getPointY());

    }

    @Test
    public void getYCord() {
        ft.setYCord(4);
        assertEquals(4, ft.getYCord());
    }

    @Test
    public void movePlayers() {
        ft.addPlayer(toto);
        ft.addPlayer(tata);
        ft.movePlayers(2);
        assertEquals(1, toto.getPointY());
        assertEquals(1, tata.getPointY());
    }

    @Test
    public void setRelativePos() {
        ft.addPlayer(toto);
        ft.addPlayer(tata);
        ft.setYCord(10);
        ft.setRelativePos();
        assertEquals(9, toto.getPointY());
        assertEquals(9, tata.getPointY());
    }

}