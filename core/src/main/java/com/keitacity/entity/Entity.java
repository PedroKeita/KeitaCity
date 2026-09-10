package com.keitacity.entity;

import com.keitacity.world.World;

public abstract class Entity {
    public float x, y;

    public abstract void update(World world);
}