package com.example.foosball.database;

public interface OnCreateGameOperation extends OnDatabaseOperation {
    void onSuccess(String gameCode);
}
