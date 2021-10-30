package com.example.foosball.database;

/**
 * Classes implementing this interface can be used to receive events when a player tries to
 * join an existing game.
 */

public interface JoinGameListener extends DatabaseListener {
    /**
     * This method will be used when the lobby is full.
     */

    void onLobbyFullError();

    /**
     * This method will be used when the supplied game code does not correspond with any
     * record on the db.
     */
    void onGameDoesNotExistError();

    /**
     * This method will be used when the game has already started.
     */

    void onGameAlreadyStartedError();

    /**
     * This method will be used when the player successfully joins an existing game.
     * @param playerId Any int between 1-4, corresponds with the player key on the db.
     */

    void onSuccess(int playerId);
}
