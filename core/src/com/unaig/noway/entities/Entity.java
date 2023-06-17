package com.unaig.noway.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.unaig.noway.util.Direction;

public class Entity {

    protected float hp;
    protected int maxHp;
    protected boolean isDead;
    protected Vector2 pos;
    protected Vector2 vel;
    protected Vector2 size;
    protected boolean isAttacking;
    protected float attackCooldown;
    protected ObjectMap<String, Animation<AtlasRegion>> animations;
    protected Rectangle bounds;
    public Direction lastDir;
    public float maxVel;
    protected float stateTime;
    public int attackDamage;
    protected boolean isDamaged;
    public float timeDamageTaken;

    public Vector2 getPos() {
        return pos;
    }

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public Vector2 getVel() {
        return vel;
    }

    public void setVel(Vector2 vel) {
        this.vel = vel;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public Vector2 getSize() {
        return size;
    }

    public void setSize(Vector2 size) {
        this.size = size;
    }

    public float getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public boolean getIsDamaged() {
        return isDamaged;
    }

    public void setIsDamaged(boolean isDamaged) {
        this.isDamaged = isDamaged;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

}
