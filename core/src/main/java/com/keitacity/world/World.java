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

    public final ZoningSystem zoning;

    private float growthTimer = 0f;

    private static final float GROWTH_INTERVAL = 3f;

    public World(int width, int height) {
        grid = new Grid(width, height);
        zoning = new ZoningSystem(this);
    }

    public void update() {
        delta = Gdx.graphics.getDeltaTime();

        for (Citizen citizen : citizens) {
            citizen.update(this);
        }

        updateGrowth();
    }

    private void updateGrowth() {

        growthTimer += delta;

        if (growthTimer < GROWTH_INTERVAL) {
            return;
        }

        growthTimer = 0f;

        for (int x = 0; x < grid.width; x++) {
            for (int y = 0; y < grid.height; y++) {

                Tile tile = grid.tiles[x][y];

                if (tile.isOccupied()) {
                    continue;
                }

                if (tile.isRoad()) {
                    continue;
                }

                if (tile.getZone() == ZoneType.NONE) {
                    continue;
                }

                placeBuilding(x, y, tile.getZone());
                return;
            }
        }
    }

    public boolean placeBuilding(int x, int y, ZoneType type) {

        if (!grid.isInside(x, y)) {
            return false;
        }

        Tile tile = grid.tiles[x][y];

        if (tile.isOccupied() || tile.isRoad()) {
            return false;
        }

        tile.setOccupied(true);

        buildings.add(new Building(x, y, type));

        if (type == ZoneType.RESIDENTIAL) {
            spawnCitizens(x, y);
        }

        return true;
    }

    public boolean buildRoad(int x, int y) {

        if (!grid.isInside(x, y)) {
            return false;
        }

        Tile tile = grid.tiles[x][y];

        if (tile.isOccupied()) {
            return false;
        }

        tile.setZone(ZoneType.NONE);
        tile.setType(TileType.ROAD);

        return true;
    }

    public boolean demolish(int x, int y) {

        if (!grid.isInside(x, y)) {
            return false;
        }

        Tile tile = grid.tiles[x][y];

        if (tile.isOccupied()) {

            buildings.removeIf(building -> building.x == x && building.y == y);

            tile.setOccupied(false);
        }

        tile.setZone(ZoneType.NONE);
        tile.setType(TileType.GRASS);

        return true;
    }

    public boolean isWalkable(int x, int y) {

        if (!grid.isInside(x, y)) {
            return false;
        }

        Tile tile = grid.tiles[x][y];

        return !tile.isOccupied();
    }

    public Building findAvailableHome() {

        for (Building building : buildings) {

            if (building.type != ZoneType.RESIDENTIAL)
                continue;
            if (building.residents >= 4)
                continue;

            return building;
        }

        return null;
    }

    private void assignHome() {

        for (Citizen citizen : citizens) {

            if (citizen.hasHome())
                continue;

            Building home = findAvailableHome();

            if (home == null)
                return;

            citizen.setHome(home.x, home.y);
            home.residents++;
        }
    }

    private void spawnCitizens(int x, int y) {

        Building home = buildings.get(buildings.size() - 1);

        for (int i = 0; i < home.maxResidents; i++) {

            Citizen citizen = new Citizen(x, y);
            citizen.setHome(x, y);

            citizens.add(citizen);
            home.residents++;
        }
    }
}