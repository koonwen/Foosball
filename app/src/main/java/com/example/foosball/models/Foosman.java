package com.example.foosball.models;

public class Foosman {
    private Position pos;

    public Foosman(float x, float y) {
        this.pos = new Position(x,y);
    }
    public Position getPosition() {
        return this.pos;
    }

    @Override
    public String toString() {
        return "Ball{" +
                "pos=" + pos +
                '}';
    }
}
