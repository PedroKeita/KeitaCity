package com.keitacity.world;

public class Tile {

    public final int x;
    public final int y;

    private TileType type;
    private ZoneType zone;
    private boolean occupied;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;

        this.type = TileType.GRASS;
        this.zone = ZoneType.NONE;
        this.occupied = false;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public ZoneType getZone() {
        return zone;
    }

    public void setZone(ZoneType zone) {
        this.zone = zone;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isRoad() {
        return type == TileType.ROAD;
    }

    public boolean hasZone() {
        return zone != ZoneType.NONE;
    }
}