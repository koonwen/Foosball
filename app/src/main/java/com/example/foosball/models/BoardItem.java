package com.example.foosball.models;

/**
 * Interface for the game board.
 */
interface BoardItem {
    void setPoint(int X, int Y);

    int getPointX();

    int getPointY();

    int getWidth();

    int getHeight();
}
