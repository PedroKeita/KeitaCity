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
        // O mundo controla a ordem da simulacao: primeiro os agentes se movem,
        // depois o crescimento pode alterar a cidade para o proximo frame.
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

        // Por enquanto o crescimento e deterministico: o primeiro lote valido
        // encontrado e ocupado a cada intervalo. Uma futura politica de demanda
        // pode substituir apenas este metodo sem mexer na construcao manual.
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

        Building building = new Building(x, y, type);
        buildings.add(building);

        if (type == ZoneType.RESIDENTIAL) {
            spawnCitizens(building);
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

            citizens.removeIf(citizen -> citizen.getHomeX() == x &&
                    citizen.getHomeY() == y);

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

    private void spawnCitizens(Building home) {

        for (int i = 0; i < home.maxResidents; i++) {

            Citizen citizen = new Citizen(home.x, home.y);
            citizen.setHome(home.x, home.y);

            citizens.add(citizen);
            home.residents++;
        }
    }
}