package com.keitacity.entity;

import com.keitacity.world.Pathfinder;
import com.keitacity.world.World;
import java.util.Random;
import java.util.List;

public class Citizen extends Entity {

    private static final Random random = new Random();
    private float moveSpeed = 2f;
    private float targetPosX;
    private float targetPosY;

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

        if (!hasTarget) {
            chooseTarget(world);
            return;
        }

        move(world);
    }

    private void chooseTarget(World world) {

        targetX = random.nextInt(world.grid.width);
        targetY = random.nextInt(world.grid.height);

        path = Pathfinder.findPath(
                world,
                Math.round(x),
                Math.round(y),
                targetX,
                targetY);

        pathIndex = 0;

        if (!path.isEmpty()) {
            targetPosX = path.get(0)[0];
            targetPosY = path.get(0)[1];
            hasTarget = true;
        }
    }

    private void move(World world) {

        float dx = targetPosX - x;
        float dy = targetPosY - y;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.05f) {

            x = targetPosX;
            y = targetPosY;

            pathIndex++;

            if (pathIndex >= path.size()) {
                hasTarget = false;
                return;
            }

            targetPosX = path.get(pathIndex)[0];
            targetPosY = path.get(pathIndex)[1];

            return;
        }

        float amount = moveSpeed * world.delta;

        if (amount > distance) {
            amount = distance;
        }

        x += dx / distance * amount;
        y += dy / distance * amount;
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
