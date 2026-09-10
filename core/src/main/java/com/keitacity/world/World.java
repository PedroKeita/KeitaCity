package com.keitacity.world;

import com.keitacity.entity.Citizen;
import java.util.ArrayList;
import java.util.List;

public class World {

    public final Grid grid;
    public final List<Citizen> citizens = new ArrayList<>();

    public World(int width, int height) {
        this.grid = new Grid(width, height);
    }

    public void update() {
        for (Citizen c : citizens) {
            c.update(this);
        }
    }
}