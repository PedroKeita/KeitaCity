package com.keitacity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.keitacity.renderer.WorldRenderer;
import com.keitacity.world.World;
import com.keitacity.world.ZoneType;

/**
 * Classe principal do KeitaCity.
 *
 * Câmera isométrica ortográfica fixa,
 * inspirada no Voxel Tycoon:
 *
 * - Yaw fixo em 45° (diagonal)
 * - Pitch fixo em 30° (levemente de cima)
 * - Pan com WASD ou clique do meio
 * - Zoom com scroll / Q e E
 * - Sem rotação livre (por ora)
 */
public class KeitaCity extends ApplicationAdapter {

    private World world;
    private WorldRenderer renderer;
    private OrthographicCamera camera;
    private BuildTool currentTool = BuildTool.RESIDENTIAL;

    /*
     * Ângulos fixos da câmera isométrica.
     *
     * yaw = 45° → diagonal (visão de canto)
     * pitch = 30° → levemente acima do horizonte
     */
    private static final float YAW_DEG = 45f;
    private static final float PITCH_DEG = 30f;

    private static final float CAMERA_SPEED = 12f;
    private static final float ZOOM_SPEED = 0.8f;
    private static final float ZOOM_MIN = 3f;
    private static final float ZOOM_MAX = 30f;

    /*
     * Ponto central que a câmera observa.
     */
    private final Vector3 cameraTarget = new Vector3();

    /*
     * Plano do chão para raycasting
     * (Y = 0).
     */
    private final Plane groundPlane = new Plane(new Vector3(0f, 1f, 0f), 0f);

    private final Vector3 intersection = new Vector3();

    @Override
    public void create() {

        world = new World(20, 20);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 24f, 14f);
        camera.zoom = 8f;

        /*
         * Câmera começa olhando para o
         * centro do mapa.
         */
        cameraTarget.set(
                world.grid.width / 2f,
                0f,
                world.grid.height / 2f);

        updateCamera();

        renderer = new WorldRenderer(world, camera);

        setupInput();
    }

    /**
     * Posiciona a câmera no ângulo
     * isométrico fixo.
     *
     * Mesma matemática de antes, mas
     * yaw e pitch nunca mudam pelo input
     * do usuário — são constantes.
     */
    private void updateCamera() {

        float yawRad = (float) Math.toRadians(YAW_DEG);
        float pitchRad = (float) Math.toRadians(PITCH_DEG);

        float distance = camera.zoom * 1.8f;

        float hDist = distance * (float) Math.cos(pitchRad);
        float vDist = distance * (float) Math.sin(pitchRad);

        float camX = cameraTarget.x + hDist * (float) Math.sin(yawRad);
        float camZ = cameraTarget.z + hDist * (float) Math.cos(yawRad);
        float camY = cameraTarget.y + vDist;

        camera.position.set(camX, camY, camZ);
        camera.lookAt(cameraTarget);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    private void setupInput() {

        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(
                    int screenX, int screenY,
                    int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    handleClick(screenX, screenY);
                    return true;
                }
                return false;
            }

            @Override
            public boolean scrolled(
                    float amountX, float amountY) {
                zoom(amountY);
                return true;
            }
        });
    }

    private void zoom(float amount) {
        camera.zoom += amount * ZOOM_SPEED;
        camera.zoom = Math.max(ZOOM_MIN,
                Math.min(camera.zoom, ZOOM_MAX));
        updateCamera();
    }

    @Override
    public void render() {
        handleKeyboard();
        world.update();
        renderer.render();
    }

    private void handleKeyboard() {

        float delta = Gdx.graphics.getDeltaTime();

        /*
         * Vetores de direção relativa à
         * câmera isométrica fixa.
         *
         * Como o yaw é sempre 45°, as direções
         * WASD são fixas no mundo isométrico.
         */
        float speed = CAMERA_SPEED * delta;

        /* W — avança no mapa (nordeste iso) */
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            cameraTarget.x -= speed * 0.5f;
            cameraTarget.z -= speed * 0.5f;
            updateCamera();
        }

        /* S — recua (sudoeste iso) */
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            cameraTarget.x += speed * 0.5f;
            cameraTarget.z += speed * 0.5f;
            updateCamera();
        }

        /* A — move para oeste iso */
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            cameraTarget.x -= speed * 0.5f;
            cameraTarget.z += speed * 0.5f;
            updateCamera();
        }

        /* D — move para leste iso */
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            cameraTarget.x += speed * 0.5f;
            cameraTarget.z -= speed * 0.5f;
            updateCamera();
        }

        /* Q — zoom in */
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            zoom(-ZOOM_SPEED * delta * 3f);
        }

        /* E — zoom out */
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            zoom(ZOOM_SPEED * delta * 3f);
        }

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
                        ZoneType.RESIDENTIAL);

                break;

            case COMMERCIAL:

                world.zoning.zone(
                        gridX,
                        gridY,
                        ZoneType.COMMERCIAL);

                break;

            case INDUSTRIAL:

                world.zoning.zone(
                        gridX,
                        gridY,
                        ZoneType.INDUSTRIAL);

                break;

            case DEMOLISH:

                world.zoning.removeZone(
                        gridX,
                        gridY);

                break;

            default:
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        if (height == 0)
            return;
        camera.viewportWidth = 24f;
        camera.viewportHeight = 24f * height / width;
        updateCamera();
    }

    @Override
    public void dispose() {
        if (renderer != null)
            renderer.dispose();
    }
}