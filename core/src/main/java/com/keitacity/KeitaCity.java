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

public class KeitaCity extends ApplicationAdapter {

    private World world;
    private WorldRenderer renderer;
    private OrthographicCamera camera;

    private static final float TILE_SIZE = 3.2f;

    private static final float CAMERA_SPEED = 20f;
    private static final float ZOOM_SPEED = 1.5f;

    /*
     * Sensibilidade da rotação da câmera.
     */
    private static final float ROTATION_SPEED = 0.25f;

    /*
     * Ângulo horizontal.
     */
    private float yaw = 45f;

    /*
     * Ângulo vertical.
     *
     * 20 = olhando mais de cima
     * 70 = olhando mais de lado
     */
    private float pitch = 45f;

    private static final float MIN_PITCH = 15f;
    private static final float MAX_PITCH = 75f;

    /*
     * Posição que a câmera está observando.
     *
     * A câmera gira ao redor desse ponto.
     */
    private final Vector3 cameraTarget =
            new Vector3(30.4f, 0f, 30.4f);

    private final Plane groundPlane =
            new Plane(
                    new Vector3(0f, 1f, 0f),
                    0f
            );

    private final Vector3 intersection =
            new Vector3();

    @Override
    public void create() {

        world = new World(20, 20);

        camera = new OrthographicCamera();

        camera.setToOrtho(
                false,
                24f,
                14f
        );

        updateCamera();

        renderer = new WorldRenderer(
                world,
                camera
        );

        setupInput();
    }

    private void setupInput() {

        Gdx.input.setInputProcessor(
                new InputAdapter() {

                    @Override
                    public boolean touchDown(
                            int screenX,
                            int screenY,
                            int pointer,
                            int button
                    ) {

                        /*
                         * Botão esquerdo:
                         * coloca prédio.
                         */
                        if (button == Input.Buttons.LEFT) {

                            placeBuildingAtMouse(
                                    screenX,
                                    screenY
                            );

                            return true;
                        }

                        return true;
                    }

                    @Override
                    public boolean touchDragged(
                            int screenX,
                            int screenY,
                            int pointer
                    ) {

                        /*
                         * BOTÃO DIREITO:
                         *
                         * segura RMB e move o mouse
                         * para girar a câmera.
                         */
                        if (Gdx.input.isButtonPressed(
                                Input.Buttons.RIGHT
                        )) {

                            rotateCamera();
                        }

                        return true;
                    }

                    @Override
                    public boolean scrolled(
                            float amountX,
                            float amountY
                    ) {

                        /*
                         * Scroll para cima:
                         * aproxima.
                         *
                         * Scroll para baixo:
                         * afasta.
                         */
                        camera.zoom +=
                                amountY * ZOOM_SPEED;

                        camera.zoom = Math.max(
                                4f,
                                Math.min(
                                        camera.zoom,
                                        40f
                                )
                        );

                        camera.update();

                        return true;
                    }
                }
        );
    }

    /**
     * Rotaciona a câmera usando o movimento
     * do mouse.
     */
    private void rotateCamera() {

        float mouseX =
                Gdx.input.getDeltaX();

        float mouseY =
                Gdx.input.getDeltaY();

        /*
         * Movimento horizontal do mouse
         * gira horizontalmente.
         */
        yaw -= mouseX * ROTATION_SPEED;

        /*
         * Movimento vertical do mouse
         * gira verticalmente.
         */
        pitch += mouseY * ROTATION_SPEED;

        /*
         * Impede a câmera de virar de cabeça
         * para baixo.
         */
        pitch = Math.max(
                MIN_PITCH,
                Math.min(
                        pitch,
                        MAX_PITCH
                )
        );

        updateCamera();
    }

    /**
     * Atualiza a posição e direção da câmera
     * baseado no yaw e pitch.
     */
    private void updateCamera() {

        float yawRad =
                (float) Math.toRadians(yaw);

        float pitchRad =
                (float) Math.toRadians(pitch);

        /*
         * Distância da câmera até o alvo.
         *
         * Quanto maior o zoom, mais distante
         * fica a câmera.
         */
        float distance =
                camera.zoom * 1.8f;

        float horizontalDistance =
                distance *
                        (float) Math.cos(pitchRad);

        float verticalDistance =
                distance *
                        (float) Math.sin(pitchRad);

        /*
         * Calcula posição X/Z da câmera.
         */
        float cameraX =
                cameraTarget.x
                        + horizontalDistance
                        * (float) Math.sin(yawRad);

        float cameraZ =
                cameraTarget.z
                        + horizontalDistance
                        * (float) Math.cos(yawRad);

        float cameraY =
                cameraTarget.y
                        + verticalDistance;

        camera.position.set(
                cameraX,
                cameraY,
                cameraZ
        );

        /*
         * A câmera sempre olha para
         * o centro do mapa.
         */
        camera.lookAt(
                cameraTarget
        );

        camera.up.set(
                Vector3.Y
        );

        camera.update();
    }

    /**
     * Clique esquerdo no mapa.
     */
    private void placeBuildingAtMouse(
            int screenX,
            int screenY
    ) {

        Ray ray =
                camera.getPickRay(
                        screenX,
                        screenY
                );

        /*
         * Descobre onde o mouse atingiu
         * o chão.
         */
        if (!Intersector.intersectRayPlane(
                ray,
                groundPlane,
                intersection
        )) {

            return;
        }

        /*
         * Converte mundo 3D -> Grid.
         */
        int gridX =
                (int) Math.floor(
                        (intersection.x
                                + TILE_SIZE / 2f)
                                / TILE_SIZE
                );

        int gridY =
                (int) Math.floor(
                        (intersection.z
                                + TILE_SIZE / 2f)
                                / TILE_SIZE
                );

        /*
         * Tenta colocar o prédio.
         */
        boolean placed =
                world.placeBuilding(
                        gridX,
                        gridY
                );

        if (placed) {

            System.out.println(
                    "Predio colocado em: "
                            + gridX
                            + ", "
                            + gridY
            );

        } else {

            System.out.println(
                    "Nao foi possivel colocar "
                            + "predio em: "
                            + gridX
                            + ", "
                            + gridY
            );
        }
    }

    @Override
    public void render() {

        handleKeyboard();

        world.update();

        renderer.render();
    }

    private void handleKeyboard() {

        float delta =
                Gdx.graphics.getDeltaTime();

        /*
         * Direção horizontal para frente.
         */
        Vector3 forward =
                new Vector3(
                        camera.direction.x,
                        0f,
                        camera.direction.z
                );

        if (forward.len2() > 0.001f) {
            forward.nor();
        }

        /*
         * Direção lateral.
         */
        Vector3 right =
                new Vector3(forward)
                        .crs(Vector3.Y)
                        .nor();

        /*
         * W
         */
        if (Gdx.input.isKeyPressed(
                Input.Keys.W
        )) {

            cameraTarget.mulAdd(
                    forward,
                    CAMERA_SPEED * delta
            );

            updateCamera();
        }

        /*
         * S
         */
        if (Gdx.input.isKeyPressed(
                Input.Keys.S
        )) {

            cameraTarget.mulAdd(
                    forward,
                    -CAMERA_SPEED * delta
            );

            updateCamera();
        }

        /*
         * A
         */
        if (Gdx.input.isKeyPressed(
                Input.Keys.A
        )) {

            cameraTarget.mulAdd(
                    right,
                    -CAMERA_SPEED * delta
            );

            updateCamera();
        }

        /*
         * D
         */
        if (Gdx.input.isKeyPressed(
                Input.Keys.D
        )) {

            cameraTarget.mulAdd(
                    right,
                    CAMERA_SPEED * delta
            );

            updateCamera();
        }

        /*
         * Q
         * Zoom in.
         */
        if (Gdx.input.isKeyPressed(
                Input.Keys.Q
        )) {

            camera.zoom -=
                    ZOOM_SPEED * delta;

            camera.zoom = Math.max(
                    4f,
                    camera.zoom
            );

            updateCamera();
        }

        /*
         * E
         * Zoom out.
         */
        if (Gdx.input.isKeyPressed(
                Input.Keys.E
        )) {

            camera.zoom +=
                    ZOOM_SPEED * delta;

            camera.zoom = Math.min(
                    40f,
                    camera.zoom
            );

            updateCamera();
        }
    }

    @Override
    public void resize(
            int width,
            int height
    ) {

        if (height == 0) {
            return;
        }

        camera.viewportWidth = 24f;

        camera.viewportHeight =
                24f *
                height /
                width;

        camera.update();
    }

    @Override
    public void dispose() {

        if (renderer != null) {
            renderer.dispose();
        }
    }
}