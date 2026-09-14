package com.keitacity.renderer;

import com.badlogic.gdx.graphics.Color;

/**
 * Paleta de cores dos blocos do KeitaCity.
 *
 * Inspirada no estilo Voxel Tycoon:
 * cores saturadas e limpas, sem texturas
 * no início — só cores sólidas por face.
 */
public class VoxelPalette {

        /** Chão padrão — grama verde */
        public static final Color GRASS = new Color(0.36f, 0.73f, 0.35f, 1f);

        /** Estrada — asfalto cinza escuro */
        public static final Color ROAD = new Color(0.30f, 0.30f, 0.32f, 1f);

        /** Prédio residencial — bege/creme */
        public static final Color BUILDING_RESIDENTIAL = new Color(0.93f, 0.87f, 0.72f, 1f);

        /** Prédio comercial — azul claro */
        public static final Color BUILDING_COMMERCIAL = new Color(0.52f, 0.74f, 0.92f, 1f);

        /** Prédio industrial — laranja */
        public static final Color BUILDING_INDUSTRIAL = new Color(0.92f, 0.60f, 0.28f, 1f);

        /** Cidadão — vermelho vivo */
        public static final Color CITIZEN = new Color(0.95f, 0.25f, 0.25f, 1f);

        /** Água */
        public static final Color WATER = new Color(0.25f, 0.55f, 0.90f, 1f);

        /** Areia */
        public static final Color SAND = new Color(0.93f, 0.85f, 0.60f, 1f);

        public static final Color ZONE_RESIDENTIAL = new Color(0.25f, 0.85f, 0.35f, 1f);

        public static final Color ZONE_COMMERCIAL = new Color(0.25f, 0.55f, 0.95f, 1f);

        public static final Color ZONE_INDUSTRIAL = new Color(0.95f, 0.60f, 0.20f, 1f);
}