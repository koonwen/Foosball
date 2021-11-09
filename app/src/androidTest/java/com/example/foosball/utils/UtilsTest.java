package com.example.foosball.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {
    @Test
    public void setGameCode_success() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String gameCode = "ABCDEF";
        Utils.setGameCode(appContext, gameCode);
        assertEquals(gameCode, Utils.getGameCode(appContext));
    }

    @Test
    public void setPlayerId_success() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int playerId = 2;
        Utils.setPlayerId(appContext, playerId);
        assertEquals(playerId, Utils.getPlayerId(appContext));
    }

    @Test
    public void isGameHost_success() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        int playerId = 1;
        Utils.setPlayerId(appContext, playerId);
        assertTrue(Utils.isGameHost(appContext));
    }
}
