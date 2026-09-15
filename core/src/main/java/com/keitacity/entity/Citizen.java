package com.keitacity.entity;

import com.keitacity.world.Pathfinder;
import com.keitacity.world.World;
import java.util.Random;
import java.util.List;

public class Citizen extends Entity {

    private static final Random random = new Random();
    private float moveTimer = 0f;
    private static final float MOVE_INTERVAL = 0.5f;

    private int targetX;
    private int targetY;
    private boolean hasTarget = false;

    private List<int[]> path = new java.util.ArrayList<>();
    private int pathIndex = 0;

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

        if (!hasTarget) {
            targetX = random.nextInt(world.grid.width);
            targetY = random.nextInt(world.grid.height);
            hasTarget = true;
            path = Pathfinder.findPath(
                    world,
                    (int) x,
                    (int) y,
                    targetX,
                    targetY);
            pathIndex = 0;
        }

        if (pathIndex >= path.size()) {
            hasTarget = false;
            return;
        }

        int[] next = path.get(pathIndex);

        if (world.isWalkable(next[0], next[1])) {
            x = next[0];
            y = next[1];
            pathIndex++;
            return;
        }

        hasTarget = false;
        path.clear();
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public boolean hasTarget() {
        return hasTarget;
    }

    public List<int[]> getPath() {
        return path;
    }
}
