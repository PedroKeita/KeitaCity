package com.keitacity.world;

public class Tile {

    public final int x;
    public final int y;
    public boolean occupied;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.occupied = false;
    }
}