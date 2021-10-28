package com.example.foosball.models;

import android.graphics.Bitmap;
import android.graphics.Rect;

interface BoardItem {
    public void setPoint(int X, int Y);
    public int getPointX();
    public int getPointY();
    public int getWidth();
    public int getHeight();
}