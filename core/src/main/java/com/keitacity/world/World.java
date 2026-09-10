package com.keitacity.world;

import com.badlogic.gdx.Gdx;
import com.keitacity.entity.Building;
import com.keitacity.entity.Citizen;
import java.util.ArrayList;
import java.util.List;

public class World {

    public final Grid grid;
    public final List<Citizen> citizens = new ArrayList<>();
    public final List<Building> buildings = new ArrayList<>();
    public float delta;

    public World(int width, int height) {
        this.grid = new Grid(width, height);
        citizens.add(new Citizen(width / 2, height / 2));
    }

    public void update() {
        delta = Gdx.graphics.getDeltaTime();
        for (Citizen c : citizens) {
            c.update(this);
        }
    }

    public boolean placeBuilding(int x, int y) {
        if (x < 0 || x >= grid.width || y < 0 || y >= grid.height) return false;
        if (grid.tiles[x][y].occupied) return false;

        grid.tiles[x][y].occupied = true;
        buildings.add(new Building(x, y));
        return true;
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= grid.width || y < 0 || y >= grid.height) return false;
        return !grid.tiles[x][y].occupied;
    }
}