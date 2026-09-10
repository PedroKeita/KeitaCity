package com.keitacity.world;

import com.badlogic.gdx.Gdx;
import com.keitacity.entity.Citizen;
import java.util.ArrayList;
import java.util.List;

public class World {

    public final Grid grid;
    public final List<Citizen> citizens = new ArrayList<>();
    public float delta;

    public World(int width, int height) {
        this.grid = new Grid(width, height);

        // adiciona um cidadão no centro do grid
        citizens.add(new Citizen(width / 2, height / 2));
    }

    public void update() {
        delta = Gdx.graphics.getDeltaTime();
        for (Citizen c : citizens) {
            c.update(this);
        }
    }
}