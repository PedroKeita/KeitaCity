package com.keitacity.entity;

import com.keitacity.world.Pathfinder;
import com.keitacity.world.World;
import com.keitacity.world.ZoneType;
import com.keitacity.entity.Building;

import java.util.Random;
import java.util.List;

public class Citizen extends Entity {

    private static final Random random = new Random();
    private float moveSpeed = 2f;
    private float targetPosX;
    private float targetPosY;

    private Activity activity = Activity.HOME;
    private int workX = -1;
    private int workY = -1;
    private int shopX = -1;
    private int shopY = -1;

    private int targetX;
    private int targetY;
    private boolean hasTarget = false;

    private List<int[]> path = new java.util.ArrayList<>();
    private int pathIndex = 0;

    private int homeX = -1;
    private int homeY = -1;

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

        if (activity == Activity.HOME) {

            Building work = findWork(world);

            if (work == null) {
                chooseRandomTarget(world);
                return;
            }

            targetX = work.x;
            targetY = work.y;
            activity = Activity.WORK;
            createPath(world);
            return;
        }

        if (activity == Activity.WORK) {

            Building shop = findShop(world);

            if (shop == null) {
                activity = Activity.HOME;
                targetX = homeX;
                targetY = homeY;
                createPath(world);
                return;
            }

            targetX = shop.x;
            targetY = shop.y;
            activity = Activity.SHOPPING;
            createPath(world);
            return;
        }

        if (activity == Activity.SHOPPING) {

            targetX = homeX;
            targetY = homeY;
            activity = Activity.HOME;
            createPath(world);
        }
    }

    private void chooseRandomTarget(World world) {

        targetX = random.nextInt(world.grid.width);
        targetY = random.nextInt(world.grid.height);

        createPath(world);
    }

    private void createPath(World world) {

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

    private Building findWork(World world) {

        List<Building> options = new java.util.ArrayList<>();

        for (Building building : world.buildings) {
            if (building.type == ZoneType.COMMERCIAL ||
                    building.type == ZoneType.INDUSTRIAL) {
                options.add(building);
            }
        }

        if (options.isEmpty()) {
            return null;
        }

        return options.get(random.nextInt(options.size()));
    }

    private Building findShop(World world) {

        List<Building> options = new java.util.ArrayList<>();

        for (Building building : world.buildings) {
            if (building.type == ZoneType.COMMERCIAL) {
                options.add(building);
            }
        }

        if (options.isEmpty()) {
            return null;
        }

        return options.get(random.nextInt(options.size()));
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

    public void setHome(int x, int y) {
        homeX = x;
        homeY = y;
    }

    public int getHomeX() {
        return homeX;
    }

    public int getHomeY() {
        return homeY;
    }

    public boolean hasHome() {
        return homeX >= 0 && homeY >= 0;
    }
}
