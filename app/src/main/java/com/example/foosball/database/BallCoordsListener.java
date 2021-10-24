package com.example.foosball.database;

public interface BallCoordsListener extends DatabaseListener {
    void onSuccess(int x, int y, int vx, int vy);
}
