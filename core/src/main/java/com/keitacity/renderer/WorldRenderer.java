package com.keitacity.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.keitacity.entity.Building;
import com.keitacity.entity.Citizen;
import com.keitacity.world.World;

import java.util.ArrayList;
import java.util.List;

public class WorldRenderer {

    private final World world;
    private final OrthographicCamera camera;

    private final ModelBatch modelBatch;
    private final Environment environment;

    private final Model predioModel;
    private final Model cidadaoModel;
    private final Model estradaModel;

    private final List<ModelInstance> roadInstances =
            new ArrayList<>();

    private final List<ModelInstance> buildingInstances =
            new ArrayList<>();

    private final List<ModelInstance> citizenInstances =
            new ArrayList<>();

    /*
     * O estrada.obj atual possui aproximadamente
     * 3.2 x 3.2 unidades.
     */
    private static final float TILE_SIZE = 3.2f;

    /*
     * Os modelos atuais de prédio e cidadão são
     * cubos de aproximadamente 0.1 unidade.
     *
     * Essas escalas são temporárias para torná-los
     * visíveis na cena.
     */
    private static final float BUILDING_SCALE = 20f;
    private static final float CITIZEN_SCALE = 5f;

    public WorldRenderer(
            World world,
            OrthographicCamera camera
    ) {

        this.world = world;
        this.camera = camera;

        modelBatch = new ModelBatch();

        environment = new Environment();

        environment.set(
                new ColorAttribute(
                        ColorAttribute.AmbientLight,
                        0.65f,
                        0.65f,
                        0.65f,
                        1f
                )
        );

        environment.add(
                new DirectionalLight().set(
                        1f,
                        1f,
                        1f,
                        -1f,
                        -0.8f,
                        -0.2f
                )
        );

        ObjLoader loader = new ObjLoader();

        predioModel = loader.loadModel(
                Gdx.files.internal(
                        "models/predio.obj"
                )
        );

        cidadaoModel = loader.loadModel(
                Gdx.files.internal(
                        "models/cidadao.obj"
                )
        );

        estradaModel = loader.loadModel(
                Gdx.files.internal(
                        "models/estrada.obj"
                )
        );

        createRoads();
    }

    private void createRoads() {

        roadInstances.clear();

        for (int x = 0; x < world.grid.width; x++) {

            for (int y = 0; y < world.grid.height; y++) {

                ModelInstance instance =
                        new ModelInstance(estradaModel);

                float worldX = x * TILE_SIZE;
                float worldZ = y * TILE_SIZE;

                instance.transform.setToTranslation(
                        worldX,
                        0f,
                        worldZ
                );

                roadInstances.add(instance);
            }
        }
    }

    public void render() {

        Gdx.gl.glClearColor(
                0.45f,
                0.65f,
                0.85f,
                1f
        );

        Gdx.gl.glClear(
                GL20.GL_COLOR_BUFFER_BIT |
                GL20.GL_DEPTH_BUFFER_BIT
        );

        Gdx.gl.glEnable(
                GL20.GL_DEPTH_TEST
        );

        updateBuildings();
        updateCitizens();

        modelBatch.begin(camera);

        for (ModelInstance instance : roadInstances) {
            modelBatch.render(
                    instance,
                    environment
            );
        }

        for (ModelInstance instance : buildingInstances) {
            modelBatch.render(
                    instance,
                    environment
            );
        }

        for (ModelInstance instance : citizenInstances) {
            modelBatch.render(
                    instance,
                    environment
            );
        }

        modelBatch.end();
    }

    private void updateBuildings() {

        buildingInstances.clear();

        for (Building building : world.buildings) {

            ModelInstance instance =
                    new ModelInstance(predioModel);

            float worldX =
                    building.x * TILE_SIZE;

            float worldZ =
                    building.y * TILE_SIZE;

            instance.transform.setToTranslation(
                    worldX,
                    0.1f,
                    worldZ
            );

            instance.transform.scale(
                    BUILDING_SCALE,
                    BUILDING_SCALE,
                    BUILDING_SCALE
            );

            buildingInstances.add(instance);
        }
    }

    private void updateCitizens() {

        citizenInstances.clear();

        for (Citizen citizen : world.citizens) {

            ModelInstance instance =
                    new ModelInstance(cidadaoModel);

            float worldX =
                    citizen.x * TILE_SIZE;

            float worldZ =
                    citizen.y * TILE_SIZE;

            instance.transform.setToTranslation(
                    worldX,
                    0.25f,
                    worldZ
            );

            instance.transform.scale(
                    CITIZEN_SCALE,
                    CITIZEN_SCALE,
                    CITIZEN_SCALE
            );

            citizenInstances.add(instance);
        }
    }

    public void dispose() {

        modelBatch.dispose();

        predioModel.dispose();
        cidadaoModel.dispose();
        estradaModel.dispose();
    }
}