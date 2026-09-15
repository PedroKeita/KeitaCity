package com.keitacity.world;

public class ZoningSystem {

    private final World world;

    public ZoningSystem(World world) {
        this.world = world;
    }

    public boolean zone(int x, int y, ZoneType zoneType) {

        if (!isInside(x, y)) {
            return false;
        }

        Tile tile = world.grid.tiles[x][y];

        // Não pode zonear uma estrada.
        if (tile.isRoad()) {
            return false;
        }

        tile.setZone(zoneType);

        return true;
    }

    public boolean removeZone(int x, int y) {

        if (!isInside(x, y)) {
            return false;
        }

        Tile tile = world.grid.tiles[x][y];

        tile.setZone(ZoneType.NONE);

        return true;
    }

    private boolean isInside(int x, int y) {

        return x >= 0
                && x < world.grid.width
                && y >= 0
                && y < world.grid.height;
    }
}