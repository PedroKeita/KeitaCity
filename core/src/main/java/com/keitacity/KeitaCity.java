package com.keitacity;

import com.badlogic.gdx.ApplicationAdapter;
import com.keitacity.renderer.WorldRenderer;
import com.keitacity.world.World;

public class KeitaCity extends ApplicationAdapter {

    private World world;
    private WorldRenderer renderer;

    @Override
    public void create() {
        world = new World(20, 20);
        renderer = new WorldRenderer(world);
    }

    @Override
    public void render() {
        world.update();
        renderer.render();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}