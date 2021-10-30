package com.example.foosball.database;

import java.util.ArrayList;

/**
 * Classes implementing this interface can be used to receive events about any updates to the
 * game status.
 */

public interface GameStatusListener extends DatabaseListener {
    /**
     * This method will be called in the event that accessing the game status node on the db
     * is successful.
     *
     * @param playerNames List of player names.
     * @param evenPlayers Whether there is an even number of players.
     * @param gameStarted Whether the game has started.
     * @param gameEnded Whether the game has ended.
     */
    void onSuccess(ArrayList<String> playerNames, Boolean evenPlayers,
                   Boolean gameStarted, Boolean gameEnded);
}
