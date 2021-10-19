package com.example.foosball.database;

import java.util.ArrayList;

public interface OnGetGameStatusOperation extends OnDatabaseOperation {
    void onSuccess(ArrayList<String> playerNames, Boolean evenPlayers,
                   Boolean gameStarted, Boolean gameEnded);
}
