package com.example.foosball.database;

public interface OnJoinGameOperation extends OnDatabaseOperation {
    void onLobbyFullError();

    void onGameDoesNotExistError();

    void onGameAlreadyStartedError();

    void onSuccess(int playerId);
}
