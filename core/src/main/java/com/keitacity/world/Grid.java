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

    public boolean isInside(int x, int y) {
        // Mantem a validacao de limites em um unico lugar para evitar acessos
        // invalidos ao array durante cliques, movimento e pathfinding.
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Tile getTile(int x, int y) {
        // O chamador deve usar isInside antes deste acesso; aqui nao duplicamos
        // a validacao para manter o acesso simples nos loops internos.
        return tiles[x][y];
    }
}