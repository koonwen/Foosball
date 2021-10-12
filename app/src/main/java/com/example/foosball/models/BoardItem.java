package com.example.foosball.models;

import android.graphics.Rect;

interface BoardItem {

    public void setPoint(int X, int Y);
    public int getPointX();
    public int getPointY();
    public Rect getBounds();
    public void setBounds(Rect bounds);
    public int getWidth();
    public int getHeight();
}