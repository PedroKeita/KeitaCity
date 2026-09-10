package com.keitacity.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.keitacity.entity.Building;
import com.keitacity.entity.Citizen;
import com.keitacity.world.Grid;
import com.keitacity.world.World;

public class WorldRenderer {

    private final World world;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    private final Texture predioTex;
    private final Texture cidadaoTex;
    private final Texture estradaTex;

    private static final int TILE_SIZE = 64;
    private static final Color BG_COLOR = new Color(0.44f, 0.44f, 0.44f, 1f); // cinza do MagicaVoxel

    public WorldRenderer(World world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();

        predioTex  = loadWithColorKey("sprites/predio.png");
        cidadaoTex = loadWithColorKey("sprites/cidadao.png");
        estradaTex = loadWithColorKey("sprites/estrada.png");
    }

    // remove o fundo cinza do MagicaVoxel tornando-o transparente
    private Texture loadWithColorKey(String path) {
        Pixmap raw = new Pixmap(Gdx.files.internal(path));
        Pixmap out = new Pixmap(raw.getWidth(), raw.getHeight(), Pixmap.Format.RGBA8888);

        int bgR = 112, bgG = 112, bgB = 112; // RGB do cinza #707070
        int threshold = 30; // tolerância para variações de sombra

        for (int x = 0; x < raw.getWidth(); x++) {
            for (int y = 0; y < raw.getHeight(); y++) {
                int pixel = raw.getPixel(x, y);
                int r = (pixel >> 24) & 0xFF;
                int g = (pixel >> 16) & 0xFF;
                int b = (pixel >> 8)  & 0xFF;

                boolean isBg = Math.abs(r - bgR) < threshold &&
                               Math.abs(g - bgG) < threshold &&
                               Math.abs(b - bgB) < threshold;

                if (isBg) {
                    out.drawPixel(x, y, 0x00000000); // transparente
                } else {
                    out.drawPixel(x, y, pixel);
                }
            }
        }

        Texture tex = new Texture(out);
        raw.dispose();
        out.dispose();
        return tex;
    }

    public void render() {
        Gdx.gl.glClearColor(0.2f, 0.5f, 0.2f, 1f); // fundo verde (grama)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Grid grid = world.grid;

        // desenha estrada em todos os tiles
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int x = 0; x < grid.width; x++) {
            for (int y = 0; y < grid.height; y++) {
                batch.draw(estradaTex,
                    x * TILE_SIZE,
                    y * TILE_SIZE,
                    TILE_SIZE,
                    TILE_SIZE
                );
            }
        }

        // desenha prédios
        for (Building b : world.buildings) {
            batch.draw(predioTex,
                b.x * TILE_SIZE,
                b.y * TILE_SIZE,
                TILE_SIZE,
                TILE_SIZE
            );
        }

        // desenha cidadãos
        for (Citizen c : world.citizens) {
            batch.draw(cidadaoTex,
                c.x * TILE_SIZE,
                c.y * TILE_SIZE,
                TILE_SIZE,
                TILE_SIZE
            );
        }

        batch.end();
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        predioTex.dispose();
        cidadaoTex.dispose();
        estradaTex.dispose();
    }
}