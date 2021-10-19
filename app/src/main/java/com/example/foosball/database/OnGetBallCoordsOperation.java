package com.example.foosball.database;

public interface OnGetBallCoordsOperation extends OnDatabaseOperation {
    void onSuccess(int x, int y);
}
