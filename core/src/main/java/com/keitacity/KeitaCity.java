package com.keitacity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.Vector3;
import com.keitacity.renderer.WorldRenderer;
import com.keitacity.world.World;
import com.keitacity.world.ZoneType;

public class KeitaCity extends ApplicationAdapter {

    private World world;
    private WorldRenderer renderer;
    private OrthographicCamera camera;

    private BuildTool currentTool = BuildTool.RESIDENTIAL;

    private float yawDeg = 45f;

    private static final float PITCH_DEG = 30f;
    private static final float CAMERA_DISTANCE = 25f;
    private static final float CAMERA_SPEED = 12f;
    private static final float ROTATION_SPEED = 90f;
    private static final float ZOOM_SPEED = 0.8f;
    private static final float ZOOM_MIN = 3f;
    private static final float ZOOM_MAX = 30f;

    private final Vector3 cameraTarget = new Vector3();
    private final Vector3 intersection = new Vector3();

    private final Plane groundPlane =
            new Plane(new Vector3(0f, 1f, 0f), 0f);

    @Override
    public void create() {

        world = new World(20, 20);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 24f, 14f);
        camera.zoom = 8f;

        cameraTarget.set(
                world.grid.width / 2f,
                0f,
                world.grid.height / 2f
        );

        updateCamera();

        renderer = new WorldRenderer(world, camera);

        setupInput();
    }

    private void updateCamera() {

        float yaw = (float) Math.toRadians(yawDeg);
        float pitch = (float) Math.toRadians(PITCH_DEG);

        float horizontalDistance =
                CAMERA_DISTANCE * (float) Math.cos(pitch);

        float verticalDistance =
                CAMERA_DISTANCE * (float) Math.sin(pitch);

        float camX =
                cameraTarget.x +
                horizontalDistance * (float) Math.sin(yaw);

        float camZ =
                cameraTarget.z +
                horizontalDistance * (float) Math.cos(yaw);

        float camY =
                cameraTarget.y + verticalDistance;

        camera.position.set(camX, camY, camZ);

        camera.lookAt(cameraTarget);

        camera.up.set(Vector3.Y);

        camera.near = 0.1f;
        camera.far = 1000f;

        camera.update();
    }

    private void setupInput() {

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(
                    int screenX,
                    int screenY,
                    int pointer,
                    int button) {

                if (button == Input.Buttons.LEFT) {
                    handleClick(screenX, screenY);
                    return true;
                }

                return false;
            }

            @Override
            public boolean scrolled(
                    float amountX,
                    float amountY) {

                zoom(-amountY);

                return true;
            }
        });
    }

    private void zoom(float amount) {

        camera.zoom += amount * ZOOM_SPEED;

        if (camera.zoom < ZOOM_MIN) {
            camera.zoom = ZOOM_MIN;
        }

        if (camera.zoom > ZOOM_MAX) {
            camera.zoom = ZOOM_MAX;
        }

        camera.update();
    }

    private void handleKeyboard() {

        float delta = Gdx.graphics.getDeltaTime();
        float speed = CAMERA_SPEED * delta;

        boolean shift =
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        boolean ctrl =
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);

        if (shift) {

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                zoom(ZOOM_SPEED * delta * 3f);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                zoom(-ZOOM_SPEED * delta * 3f);
            }

            return;
        }

        if (ctrl) {

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                yawDeg -= ROTATION_SPEED * delta;
                updateCamera();
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                yawDeg += ROTATION_SPEED * delta;
                updateCamera();
            }

            return;
        }

        float yaw = (float) Math.toRadians(yawDeg);

        float forwardX = -(float) Math.sin(yaw);
        float forwardZ = -(float) Math.cos(yaw);

        float rightX = (float) Math.cos(yaw);
        float rightZ = -(float) Math.sin(yaw);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            cameraTarget.x += forwardX * speed;
            cameraTarget.z += forwardZ * speed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            cameraTarget.x -= forwardX * speed;
            cameraTarget.z -= forwardZ * speed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            cameraTarget.x -= rightX * speed;
            cameraTarget.z -= rightZ * speed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            cameraTarget.x += rightX * speed;
            cameraTarget.z += rightZ * speed;
        }

        updateCamera();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            currentTool = BuildTool.RESIDENTIAL;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            currentTool = BuildTool.COMMERCIAL;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            currentTool = BuildTool.INDUSTRIAL;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            currentTool = BuildTool.DEMOLISH;
        }
    }

    private void handleClick(int screenX, int screenY) {

        Ray ray = camera.getPickRay(screenX, screenY);

        if (!Intersector.intersectRayPlane(
                ray,
                groundPlane,
                intersection)) {
            return;
        }

        int gridX = Math.round(intersection.x);
        int gridY = Math.round(intersection.z);

        switch (currentTool) {

            case RESIDENTIAL:
                world.zoning.zone(
                        gridX,
                        gridY,
                        ZoneType.RESIDENTIAL
                );
                break;

            case COMMERCIAL:
                world.zoning.zone(
                        gridX,
                        gridY,
                        ZoneType.COMMERCIAL
                );
                break;

            case INDUSTRIAL:
                world.zoning.zone(
                        gridX,
                        gridY,
                        ZoneType.INDUSTRIAL
                );
                break;

            case DEMOLISH:
                world.zoning.removeZone(
                        gridX,
                        gridY
                );
                break;

            default:
                break;
        }
    }

    @Override
    public void render() {

        handleKeyboard();

        world.update();

        renderer.render();
    }

    @Override
    public void resize(int width, int height) {

        if (height == 0) {
            return;
        }

        camera.viewportWidth = 24f;
        camera.viewportHeight = 24f * height / width;

        updateCamera();
    }

    @Override
    public void dispose() {

        if (renderer != null) {
            renderer.dispose();
        }
    }
}