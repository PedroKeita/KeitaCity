package com.keitacity.entity;

import com.keitacity.world.ZoneType;

public class Building {

    public final int x;
    public final int y;

    public final ZoneType type;

    // Nível atual do prédio.
    // Por enquanto toda construção começa no nível 1.
    public int level;

    public Building(int x, int y, ZoneType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.level = 1;
    }
}

