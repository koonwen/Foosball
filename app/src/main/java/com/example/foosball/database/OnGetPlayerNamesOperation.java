package com.example.foosball.database;

import java.util.ArrayList;

public interface OnGetPlayerNamesOperation extends OnDatabaseOperation {
    void onSuccess(ArrayList<String> playerNames);
}
