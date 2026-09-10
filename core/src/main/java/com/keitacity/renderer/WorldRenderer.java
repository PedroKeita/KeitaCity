package com.keitacity.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.keitacity.entity.Citizen;
import com.keitacity.world.Grid;
import com.keitacity.world.World;

public class WorldRenderer {

    private final World world;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;

    private static final int TILE_SIZE = 32;

    public WorldRenderer(World world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render() {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Grid grid = world.grid;

        // desenha grid
        for (int x = 0; x < grid.width; x++) {
            for (int y = 0; y < grid.height; y++) {
                if ((x + y) % 2 == 0) {
                    shapeRenderer.setColor(Color.FOREST);
                } else {
                    shapeRenderer.setColor(Color.OLIVE);
                }
                shapeRenderer.rect(
                    x * TILE_SIZE,
                    y * TILE_SIZE,
                    TILE_SIZE - 1,
                    TILE_SIZE - 1
                );
            }
        }

        // desenha cidadãos
        shapeRenderer.setColor(Color.RED);
        for (Citizen c : world.citizens) {
            shapeRenderer.circle(
                c.x * TILE_SIZE + TILE_SIZE / 2f,
                c.y * TILE_SIZE + TILE_SIZE / 2f,
                TILE_SIZE / 3f
            );
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}