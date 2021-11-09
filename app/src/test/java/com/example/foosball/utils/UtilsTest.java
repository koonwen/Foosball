package com.example.foosball.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UtilsTest {
    @Test
    public void generateGameCode_correctLength() {
        assertEquals(Utils.NUM_CHARS_GAME_CODE, Utils.generateGameCode().length());
    }

    @Test
    public void generateGameCode_allUpperCase() {
        String gameCode = Utils.generateGameCode();
        for (int i = 0; i < gameCode.length(); i++) {
            assertTrue(Character.isUpperCase(gameCode.charAt(i)));
        }
    }
}
