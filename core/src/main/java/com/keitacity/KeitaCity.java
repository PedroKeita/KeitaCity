package com.keitacity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.keitacity.renderer.WorldRenderer;
import com.keitacity.world.World;

public class KeitaCity extends ApplicationAdapter {

    private World world;
    private WorldRenderer renderer;
    private OrthographicCamera camera;

    private static final float CAMERA_SPEED = 200f;
    private static final float ZOOM_SPEED = 0.1f;
    private static final float MIN_ZOOM = 0.3f;
    private static final float MAX_ZOOM = 3f;
    private static final int TILE_SIZE = 32;

    @Override
    public void create() {
        world = new World(20, 20);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set((20 * TILE_SIZE) / 2f, (20 * TILE_SIZE) / 2f, 0);
        camera.update();

        renderer = new WorldRenderer(world, camera);
    }

    @Override
    public void render() {
        handleInput();
        handleClick();
        world.update();
        renderer.render();
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        float speed = CAMERA_SPEED * camera.zoom;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.y += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.y -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.x -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.position.x += speed * delta;

        float scroll = Gdx.input.isKeyPressed(Input.Keys.Q) ? ZOOM_SPEED :
                       Gdx.input.isKeyPressed(Input.Keys.E) ? -ZOOM_SPEED : 0;
        camera.zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, camera.zoom + scroll * delta * 10));

        camera.update();
    }

    private void handleClick() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // converte posição do mouse para coordenadas do mundo
            Vector3 worldPos = camera.unproject(new Vector3(
                Gdx.input.getX(),
                Gdx.input.getY(),
                0
            ));

            int tileX = (int) (worldPos.x / TILE_SIZE);
            int tileY = (int) (worldPos.y / TILE_SIZE);

            world.placeBuilding(tileX, tileY);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}