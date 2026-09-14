package com.keitacity.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 * Gera modelos de cubo 3D proceduralmente.
 *
 * Cada cubo tem 3 materiais distintos para simular
 * iluminação voxel estilo Voxel Tycoon:
 *   - Topo:   mais claro  (luz direta)
 *   - Frente: tom médio   (luz lateral)
 *   - Lado:   mais escuro (sombra)
 */
public class VoxelMeshBuilder {

    /**
     * Cria um cubo 1x1x1 com iluminação
     * fake de 3 faces.
     *
     * @param baseColor cor base do bloco
     */
    public static Model createCube(Color baseColor) {

        ModelBuilder modelBuilder = new ModelBuilder();

        /*
         * Topo: 20% mais claro.
         */
        Color top = baseColor.cpy().mul(1.25f);
        top.a = 1f;

        /*
         * Frente: cor base.
         */
        Color front = baseColor.cpy();

        /*
         * Lado: 25% mais escuro.
         */
        Color side = baseColor.cpy().mul(0.75f);
        side.a = 1f;

        long attrs =
                VertexAttributes.Usage.Position |
                VertexAttributes.Usage.Normal |
                VertexAttributes.Usage.ColorPacked;

        modelBuilder.begin();

        /*
         * TOPO (Y+)
         */
        MeshPartBuilder mpb = modelBuilder.part(
                "top",
                com.badlogic.gdx.graphics.GL20.GL_TRIANGLES,
                attrs,
                new Material(ColorAttribute.createDiffuse(top))
        );
        mpb.setColor(top);
        mpb.rect(
                -0.5f, 0.5f,  0.5f,
                 0.5f, 0.5f,  0.5f,
                 0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0f, 1f, 0f
        );

        /*
         * FRENTE (Z+)
         */
        mpb = modelBuilder.part(
                "front",
                com.badlogic.gdx.graphics.GL20.GL_TRIANGLES,
                attrs,
                new Material(ColorAttribute.createDiffuse(front))
        );
        mpb.setColor(front);
        mpb.rect(
                -0.5f, -0.5f, 0.5f,
                 0.5f, -0.5f, 0.5f,
                 0.5f,  0.5f, 0.5f,
                -0.5f,  0.5f, 0.5f,
                0f, 0f, 1f
        );

        /*
         * TRÁS (Z-)
         */
        mpb = modelBuilder.part(
                "back",
                com.badlogic.gdx.graphics.GL20.GL_TRIANGLES,
                attrs,
                new Material(ColorAttribute.createDiffuse(front))
        );
        mpb.setColor(front);
        mpb.rect(
                 0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                 0.5f,  0.5f, -0.5f,
                0f, 0f, -1f
        );

        /*
         * DIREITA (X+)
         */
        mpb = modelBuilder.part(
                "right",
                com.badlogic.gdx.graphics.GL20.GL_TRIANGLES,
                attrs,
                new Material(ColorAttribute.createDiffuse(side))
        );
        mpb.setColor(side);
        mpb.rect(
                0.5f, -0.5f,  0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                1f, 0f, 0f
        );

        /*
         * ESQUERDA (X-)
         */
        mpb = modelBuilder.part(
                "left",
                com.badlogic.gdx.graphics.GL20.GL_TRIANGLES,
                attrs,
                new Material(ColorAttribute.createDiffuse(side))
        );
        mpb.setColor(side);
        mpb.rect(
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                -1f, 0f, 0f
        );

        /*
         * BASE (Y-) — geralmente não visível,
         * mas incluída para completude.
         */
        mpb = modelBuilder.part(
                "bottom",
                com.badlogic.gdx.graphics.GL20.GL_TRIANGLES,
                attrs,
                new Material(ColorAttribute.createDiffuse(side))
        );
        mpb.setColor(side);
        mpb.rect(
                 0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f, -0.5f,
                 0.5f, -0.5f, -0.5f,
                0f, -1f, 0f
        );

        return modelBuilder.end();
    }
}