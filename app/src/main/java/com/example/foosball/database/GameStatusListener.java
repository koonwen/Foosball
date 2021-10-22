package com.example.foosball.database;

import java.util.ArrayList;

public interface GameStatusListener extends DatabaseListener {
    void onSuccess(ArrayList<String> playerNames, Boolean evenPlayers,
                   Boolean gameStarted, Boolean gameEnded);
}
