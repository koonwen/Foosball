package com.example.foosball.database;

public interface JoinGameListener extends DatabaseListener {
    void onLobbyFullError();

    void onGameDoesNotExistError();

    void onGameAlreadyStartedError();

    void onSuccess(int playerId);
}
