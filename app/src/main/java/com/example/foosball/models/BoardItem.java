package com.example.foosball.models;

interface BoardItem {
    void setPoint(int X, int Y);

    int getPointX();

    int getPointY();

    int getWidth();

    int getHeight();
}
