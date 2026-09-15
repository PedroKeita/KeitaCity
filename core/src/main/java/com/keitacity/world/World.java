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

    /*
     * Tempo acumulado para crescimento
     * das zonas.
     */
    private float growthTimer = 0f;

    /*
     * Tempo necessário para tentar
     * construir uma nova casa.
     */
    private static final float GROWTH_INTERVAL = 3f;

    public World(int width, int height) {

        this.grid = new Grid(width, height);

        this.zoning = new ZoningSystem(this);
        citizens.add(new Citizen(width / 2, height / 2));
    }

    public void update() {

        delta = Gdx.graphics.getDeltaTime();

        for (Citizen c : citizens) {
            c.update(this);
        }

        updateGrowth();
    }

    /**
     * Sistema simples de crescimento urbano.
     *
     * Procura zonas residenciais livres
     * e constrói automaticamente.
     */
    private void updateGrowth() {

        growthTimer += delta;

        if (growthTimer < GROWTH_INTERVAL) {
            return;
        }

        growthTimer = 0f;

        growZones();
    }

    private void growZones() {

        for (int x = 0; x < grid.width; x++) {

            for (int y = 0; y < grid.height; y++) {

                Tile tile = grid.tiles[x][y];

                /*
                 * Não construir onde já existe
                 * alguma coisa.
                 */
                if (tile.isOccupied()) {
                    continue;
                }

                ZoneType zone = tile.getZone();

                // Só construir em zonas residenciais, comerciais ou industriais.
                if (zone == ZoneType.NONE) { 
                    continue; 
                }

                /*
                 * Constrói automaticamente de acordo com o tipo da zona.
                 */
                placeBuilding( x, y, zone );

                /*
                 * Apenas uma construção por ciclo.
                 */
                return;
            }
        }
    }

    /**
     * Cria uma construção.
     */
    public boolean placeBuilding( int x, int y,ZoneType type) {

        if (x < 0 || x >= grid.width || y < 0 || y >= grid.height) {
            return false;
        }

        Tile tile = grid.tiles[x][y];

        if (tile.isOccupied()) {
            return false;
        }

        tile.setOccupied(true);

        buildings.add(new Building(x, y,type));

        System.out.println("Construcao automatica: " + type + " em "
                + x + ", " + y);

        return true;
    }

    public boolean isWalkable(int x, int y) {

        if (x < 0 || x >= grid.width || y < 0 || y >= grid.height) {
            return false;
        }

        return !grid.tiles[x][y].isOccupied();
    }
}

