package com.keitacity.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.keitacity.entity.Building;
import com.keitacity.entity.Citizen;
import com.keitacity.world.World;
import com.keitacity.world.ZoneType;

import java.util.ArrayList;
import java.util.List;

/**
 * Renderiza o mundo usando cubos voxel
 * em projeção isométrica ortográfica.
 *
 * Estilo inspirado no Voxel Tycoon:
 * câmera fixa em 45° yaw / 30° pitch,
 * blocos coloridos sem textura (por agora).
 */
public class WorldRenderer {

        private final World world;
        private final OrthographicCamera camera;

        private final ModelBatch modelBatch;
        private final Environment environment;

        /*
         * Modelos base: um por cor.
         * Reutilizados por todas as instâncias.
         */
        private final Model groundModel;
        private final Model residentialBuildingModel;
        private final Model commercialBuildingModel;
        private final Model industrialBuildingModel;
        private final Model citizenModel;

        private final Model residentialZoneModel;
        private final Model commercialZoneModel;
        private final Model industrialZoneModel;

        /*
         * Instâncias renderizadas a cada frame.
         */
        private final List<ModelInstance> groundInstances = new ArrayList<>();

        private final List<ModelInstance> buildingInstances = new ArrayList<>();

        private final List<ModelInstance> citizenInstances = new ArrayList<>();

        private final List<ModelInstance> zoneInstances = new ArrayList<>();

        /*
         * Espaçamento entre tiles no mundo 3D.
         * 1 unidade = 1 bloco voxel.
         */
        private static final float TILE_SIZE = 1f;

        /*
         * Altura do bloco de chão.
         */
        private static final float GROUND_HEIGHT = 0.25f;

        /*
         * Altura de cada andar de prédio.
         */
        private static final float FLOOR_HEIGHT = 1f;

        /*
         * Andares do prédio padrão.
         */
        private static final int BUILDING_FLOORS = 3;

        public WorldRenderer(
                        World world,
                        OrthographicCamera camera) {
                this.world = world;
                this.camera = camera;

                residentialZoneModel = VoxelMeshBuilder.createCube(VoxelPalette.ZONE_RESIDENTIAL);

                commercialZoneModel = VoxelMeshBuilder.createCube(VoxelPalette.ZONE_COMMERCIAL);

                industrialZoneModel = VoxelMeshBuilder.createCube(VoxelPalette.ZONE_INDUSTRIAL);

                modelBatch = new ModelBatch();

                /*
                 * Ambiente com luz suave + direcional
                 * para realçar as faces do cubo.
                 */
                environment = new Environment();
                environment.set(
                                new ColorAttribute(
                                                ColorAttribute.AmbientLight,
                                                0.55f, 0.55f, 0.55f, 1f));
                environment.add(
                                new DirectionalLight().set(
                                                0.9f, 0.9f, 0.85f,
                                                -1f, -1.2f, -0.5f));

                /*
                 * Modelos gerados proceduralmente.
                 * Sem arquivos .obj — tudo em código.
                 */
                groundModel = VoxelMeshBuilder.createCube(VoxelPalette.GRASS);
                
                residentialBuildingModel = VoxelMeshBuilder.createCube(VoxelPalette.BUILDING_RESIDENTIAL);

                commercialBuildingModel = VoxelMeshBuilder.createCube(VoxelPalette.BUILDING_COMMERCIAL);

                industrialBuildingModel = VoxelMeshBuilder.createCube(VoxelPalette.BUILDING_INDUSTRIAL);

                citizenModel = VoxelMeshBuilder.createCube(VoxelPalette.CITIZEN);

                buildGroundInstances();
        }

        /**
         * Pré-cria as instâncias do chão.
         *
         * O chão não muda a cada frame, então
         * criamos uma vez só.
         */
        private void buildGroundInstances() {

                groundInstances.clear();

                for (int x = 0; x < world.grid.width; x++) {
                        for (int y = 0; y < world.grid.height; y++) {

                                ModelInstance instance = new ModelInstance(groundModel);

                                /*
                                 * Escala: tile largo e fino,
                                 * parecendo um bloco de chão voxel.
                                 */
                                instance.transform
                                                .setToTranslation(
                                                                x * TILE_SIZE,
                                                                -GROUND_HEIGHT / 2f,
                                                                y * TILE_SIZE)
                                                .scale(
                                                                TILE_SIZE,
                                                                GROUND_HEIGHT,
                                                                TILE_SIZE);

                                groundInstances.add(instance);
                        }
                }
        }

        public void render() {

                Gdx.gl.glClearColor(0.53f, 0.80f, 0.92f, 1f);
                Gdx.gl.glClear(
                                GL20.GL_COLOR_BUFFER_BIT |
                                                GL20.GL_DEPTH_BUFFER_BIT);
                Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

                updateBuildings();
                updateCitizens();

                updateZones();

                modelBatch.begin(camera);

                for (ModelInstance i : groundInstances)
                        modelBatch.render(i, environment);
                for (ModelInstance i : buildingInstances)
                        modelBatch.render(i, environment);
                for (ModelInstance i : citizenInstances)
                        modelBatch.render(i, environment);
                for (ModelInstance i : zoneInstances)
                        modelBatch.render(i, environment);

                modelBatch.end();
        }

        /**
         * Reconstrói instâncias de prédios.
         *
         * Cada prédio = N cubos empilhados
         * (um por andar).
         */
        private void updateBuildings() {

                buildingInstances.clear();

                for (Building building : world.buildings) {

                        for (int floor = 0; floor < BUILDING_FLOORS; floor++) {

                                Model model;

                                switch (building.type) {
                                        case RESIDENTIAL:
                                                model = residentialBuildingModel;
                                                break;
                                        case COMMERCIAL:
                                                model = commercialBuildingModel;
                                                break;
                                        case INDUSTRIAL:
                                                model = industrialBuildingModel;
                                                break;
                                        default:
                                                model = residentialBuildingModel;
                                }

                                ModelInstance instance = new ModelInstance(model);

                                float worldX = building.x * TILE_SIZE;
                                float worldZ = building.y * TILE_SIZE;

                                /*
                                 * Cada andar empilhado em Y.
                                 *
                                 * O chão fica em Y=0, então o
                                 * primeiro andar começa em Y=0.5
                                 * (metade do cubo acima do chão).
                                 */
                                float worldY = floor * FLOOR_HEIGHT
                                                + FLOOR_HEIGHT / 2f;

                                instance.transform
                                                .setToTranslation(
                                                                worldX,
                                                                worldY,
                                                                worldZ);

                                buildingInstances.add(instance);
                        }
                }
        }

        /**
         * Reconstrói instâncias de cidadãos.
         *
         * Cidadão = cubo pequeno andando
         * sobre o chão.
         */
        private void updateCitizens() {

                citizenInstances.clear();

                for (Citizen citizen : world.citizens) {

                        ModelInstance instance = new ModelInstance(citizenModel);

                        float worldX = citizen.x * TILE_SIZE;
                        float worldZ = citizen.y * TILE_SIZE;

                        /*
                         * Cidadão fica sobre o chão.
                         */
                        float worldY = FLOOR_HEIGHT * 0.3f;

                        instance.transform
                                        .setToTranslation(
                                                        worldX,
                                                        worldY,
                                                        worldZ)
                                        .scale(0.4f, 0.6f, 0.4f);

                        citizenInstances.add(instance);
                }
        }

        private void updateZones() {

                zoneInstances.clear();

                for (int x = 0; x < world.grid.width; x++) {
                        for (int y = 0; y < world.grid.height; y++) {

                                ZoneType zone = world.grid.tiles[x][y].getZone();

                                if (zone == ZoneType.NONE) {
                                        continue;
                                }

                                Model model;

                                switch (zone) {

                                        case RESIDENTIAL:
                                                model = residentialZoneModel;
                                                break;

                                        case COMMERCIAL:
                                                model = commercialZoneModel;
                                                break;

                                        case INDUSTRIAL:
                                                model = industrialZoneModel;
                                                break;

                                        default:
                                                continue;
                                }

                                ModelInstance instance = new ModelInstance(model);

                                instance.transform
                                                .setToTranslation(
                                                                x * TILE_SIZE,
                                                                0.15f,
                                                                y * TILE_SIZE)
                                                .scale(
                                                                TILE_SIZE,
                                                                0.1f,
                                                                TILE_SIZE);

                                zoneInstances.add(instance);
                        }
                }
        }

        public void dispose() {
                modelBatch.dispose();
                groundModel.dispose();
                residentialBuildingModel.dispose();
                commercialBuildingModel.dispose();
                industrialBuildingModel.dispose();
                citizenModel.dispose();
                residentialZoneModel.dispose();
                commercialZoneModel.dispose();
                industrialZoneModel.dispose();
        }
}