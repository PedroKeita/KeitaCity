package com.keitacity.world;

public class Grid {

    public final int width;
    public final int height;
    public final Tile[][] tiles;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y);
            }
        }
    }
}