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

        int dir = random.nextInt(4);
        int newX = (int) x + dx[dir];
        int newY = (int) y + dy[dir];

        // checa se está dentro do grid
        if (newX >= 0 && newX < world.grid.width &&
            newY >= 0 && newY < world.grid.height) {
            x = newX;
            y = newY;
        }
    }
}