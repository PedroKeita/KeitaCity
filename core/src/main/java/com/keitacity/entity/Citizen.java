package com.keitacity.entity;

import com.keitacity.world.World;
import java.util.Random;

public class Citizen extends Entity {

    private static final Random random = new Random();
    private float moveTimer = 0f;
    private static final float MOVE_INTERVAL = 0.5f;

    public Citizen(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    @Override
    public void update(World world) {
        moveTimer += world.delta;

        if (moveTimer >= MOVE_INTERVAL) {
            moveTimer = 0f;
            tryMove(world);
        }
    }

    private void tryMove(World world) {
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        // tenta direções em ordem aleatória
        int[] dirs = {0, 1, 2, 3};
        for (int i = 3; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = dirs[i]; dirs[i] = dirs[j]; dirs[j] = tmp;
        }

        for (int dir : dirs) {
            int newX = (int) x + dx[dir];
            int newY = (int) y + dy[dir];

            if (world.isWalkable(newX, newY)) {
                x = newX;
                y = newY;
                return;
            }
        }
    }
}