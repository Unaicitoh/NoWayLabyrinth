package com.unaig.noway.entities.spells;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.unaig.noway.entities.Entity;
import com.unaig.noway.entities.Player;
import com.unaig.noway.util.AttackType;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.AttackType.STRONG;
import static com.unaig.noway.util.Constants.TILE_SIZE;

public abstract class Spell extends Entity implements Poolable {

    protected static final float FRAME_DURATION = .06f;
    protected static final float FRAME_DURATION_STRONG = .075f;

    public boolean isAlive;
    private float timeAlive;
    protected Animation<AtlasRegion> animation;
    protected float velMultiplier;
    private static final float OFFSET_X = 2f;
    private static final float OFFSET_Y = 2.5f;
    private static final float LIFE_DURATION = 3f;
    private float playerMaxVel;
    protected AttackType attackType;

    protected void init(Player player, AttackType attackType) {
        isAlive = true;
        timeAlive = 0;
        if (attackType == STRONG) {
            velMultiplier = 2.0f;
            attackDamage = 25 * player.attackDamage;
        } else {
            velMultiplier = 2.5f;
            attackDamage = 10 * player.attackDamage;
        }
        pos = new Vector2(player.getPos());
        playerMaxVel = player.maxVel;
        vel = new Vector2(player.getVel().x * velMultiplier, player.getVel().y * velMultiplier);
        size = new Vector2(TILE_SIZE, TILE_SIZE);
        lastDir = player.lastDir;
        bounds = new Rectangle(pos.x + OFFSET_X, pos.y + OFFSET_Y, size.x - OFFSET_X * 2, size.y - OFFSET_Y * 2);
        this.attackType = attackType;
    }

    public void update(float delta) {
        timeAlive += delta;
        vel.x = MathUtils.clamp(vel.x, -playerMaxVel * velMultiplier, playerMaxVel * velMultiplier);
        vel.y = MathUtils.clamp(vel.y, -playerMaxVel * velMultiplier, playerMaxVel * velMultiplier);
        if (vel.x == 0 && vel.y == 0) {
            switch (lastDir) {
                case LEFT:
                    vel.x = -playerMaxVel * velMultiplier;
                    break;
                case DOWN:
                    vel.y = -playerMaxVel * velMultiplier;
                    break;
                case RIGHT:
                    vel.x = playerMaxVel * velMultiplier;
                    break;
                case UP:
                    vel.y = playerMaxVel * velMultiplier;
                    break;

            }
        } else {
            pos.x += vel.x * delta;
            pos.y += vel.y * delta;
            bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
        }

        if (GameHelper.checkCollisions(bounds) || timeAlive > LIFE_DURATION) {
            isAlive = false;
        }

    }

    public void render(SpriteBatch batch, float delta) {
        update(delta);
        batch.draw(animation.getKeyFrame(timeAlive), pos.x, pos.y, size.x, size.y);
    }

    @Override
    public void reset() {
        isAlive = true;
        timeAlive = 0;
        pos = new Vector2();
        vel = new Vector2();
        size = new Vector2();
        bounds = new Rectangle();
        velMultiplier = 0;
        attackType = null;
        animation = null;
    }

    public abstract void release();


    public int getDamage() {
        return attackDamage;
    }

    public AttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(AttackType attackType) {
        this.attackType = attackType;
    }
}
