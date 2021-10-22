package com.example.foosball.database;

/**
 * Classes implementing this interface can be used to receive events about a game creation
 * operation.
 */
public interface CreateGameListener extends DatabaseListener {
    /**
     * This method will be called in the event that the game creation operation is successful.
     *
     * @param gameCode The game code of the game that was created.
     */
    void onSuccess(String gameCode);
}
