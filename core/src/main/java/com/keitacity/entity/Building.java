package com.keitacity.entity;

import com.keitacity.world.ZoneType;

public class Building {

    public final int x;
    public final int y;

    public final ZoneType type;

    // Nível atual do prédio.
    // Por enquanto toda construção começa no nível 1.
    public int level;

    public int residents;
    public int maxResidents;

    public int workers;
    public int maxWorkers;

    public Building(int x, int y, ZoneType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.level = 1;
        this.residents = 0;
        this.workers = 0;
        this.maxWorkers = type == ZoneType.RESIDENTIAL ? 0 : 4;
        this.maxResidents = type == ZoneType.RESIDENTIAL ? 4 : 0;
    }
}
