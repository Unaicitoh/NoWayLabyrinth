package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.unaig.noway.data.Assets;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.spells.Spell;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL;
import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.Direction.*;

public class SpiderEnemy extends Enemy {

    private static final Pool<SpiderEnemy> spiderPool = new Pool<SpiderEnemy>() {
        @Override
        protected SpiderEnemy newObject() {
            return new SpiderEnemy();
        }
    };

    public static void create(PoolEngine poolEngine) {
        SpiderEnemy enemy = spiderPool.obtain();
        enemy.init();
        poolEngine.add(enemy);
    }

    protected void init() {
        maxHp = 100;
        maxVel = TILE_SIZE * 2.5f;
        attackDamage = 15;
        super.init();
        loadSpiderAnimations(animations);
    }

    @Override
    public void render(SpriteBatch batch, ShapeDrawer shaper, float delta, Player player, Array<Spell> spells) {
        update(delta, player, spells);
        checkSpiderStatus(delta);
        if (drawHp && !isDead) {
            hpbar.render(shaper, delta, pos, hp);
        }
        GameHelper.damagedEntityAnimation(this, batch, delta);
        renderSpiderAnimations(batch);

    }

    private void checkSpiderStatus(float delta) {
        if (isBurned && burnDuration >= 0) {
            updateBurnStatus(delta);

        } else if (isFrozen && frozenDuration >= 0) {
            updateFrozenStatus(delta);
        } else if (isSlowed && slowedDuration >= 0) {
            updateSlowedStatus(delta);
        } else {
            maxVel = TILE_SIZE * 2.5f;
        }
    }

    private void updateSlowedStatus(float delta) {
        slowedDuration -= delta;
        if (slowedDuration < 0) {
            slowedDuration = SLOWED_ENEMY_TIME;
            isSlowed = false;
            maxVel = TILE_SIZE * 2.5f;
        }
    }

    private void updateFrozenStatus(float delta) {
        frozenDuration -= delta;
        maxVel = 0;
        if (frozenDuration < 0) {
            frozenDuration = FROZEN_ENEMY_TIME;
            isFrozen = false;
            maxVel = TILE_SIZE * 2.5f;
        }
    }

    private void updateBurnStatus(float delta) {
        burnDuration -= delta;
        hp -= delta * 5;
        if (isSlowed) {
            maxVel = TILE_SIZE * 2.5f;
            isSlowed = false;
        }
        if (burnDuration < 0) {
            isBurned = false;
            burnDuration = BURN_ENEMY_TIME;
        }
    }

    private void renderSpiderAnimations(SpriteBatch batch) {
        if ((!isAttacking && !isDead) || (isFrozen && !isDead)) {
            if (vel.x < 0) {
                enemyAnimation(SPIDER_ANIM_LEFT, batch, LEFT);
            } else if (vel.x > 0) {
                enemyAnimation(SPIDER_ANIM_RIGHT, batch, RIGHT);
            } else if (vel.y > 0) {
                enemyAnimation(SPIDER_ANIM_UP, batch, UP);
            } else if (vel.y < 0) {
                enemyAnimation(SPIDER_ANIM_DOWN, batch, DOWN);
            } else {
                enemyStand(batch);
            }
        } else if (isAttacking && !isDead) {
            enemyAttackingAnimation(batch);

        } else {
            GameHelper.drawEntity(batch, animations.get(SPIDER_DEAD_ANIM).getKeyFrame(stateTime), pos, size);

        }
    }

    private void enemyAttackingAnimation(SpriteBatch batch) {
        if (lastDir == RIGHT)
            GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_RIGHT).getKeyFrame(stateTime), pos, size);
        else if (lastDir == LEFT)
            GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_LEFT).getKeyFrame(stateTime), pos, size);
        else if (lastDir == UP)
            GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_UP).getKeyFrame(stateTime), pos, size);
        else if (lastDir == DOWN)
            GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_DOWN).getKeyFrame(stateTime), pos, size);
    }

    private void enemyStand(SpriteBatch batch) {
        if (lastDir == RIGHT)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_RIGHT, 0), pos, size);
        else if (lastDir == LEFT)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_LEFT, 0), pos, size);
        else if (lastDir == UP)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_UP, 0), pos, size);
        else if (lastDir == DOWN)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_DOWN, 0), pos, size);
    }

    private void enemyAnimation(String spiderAnim, SpriteBatch batch, Direction dir) {
        if (isSlowed) {
            animations.get(spiderAnim).setFrameDuration(FRAME_DURATION * 2);
        } else {
            animations.get(spiderAnim).setFrameDuration(FRAME_DURATION);
        }
        GameHelper.drawEntity(batch, animations.get(spiderAnim).getKeyFrame(stateTime), pos, size);
        lastDir = dir;
    }

    private void loadSpiderAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(SPIDER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_RIGHT), LOOP));
        animations.put(SPIDER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_LEFT), LOOP));
        animations.put(SPIDER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_UP), LOOP));
        animations.put(SPIDER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_DOWN), LOOP));
        animations.put(SPIDER_ATTACK_RIGHT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_RIGHT), LOOP));
        animations.put(SPIDER_ATTACK_LEFT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_LEFT), LOOP));
        animations.put(SPIDER_ATTACK_UP, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_UP), LOOP));
        animations.put(SPIDER_ATTACK_DOWN, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_DOWN), LOOP));
        animations.put(SPIDER_DEAD_ANIM, new Animation<>(DEAD_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_DEAD_ANIM), NORMAL));
    }

    @Override
    public void release() {
        spiderPool.free(this);
    }
}
