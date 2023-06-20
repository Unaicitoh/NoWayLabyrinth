package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
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

public class ZombieEnemy extends Enemy {

    private static final Pool<ZombieEnemy> zombiePool = new Pool<ZombieEnemy>() {
        @Override
        protected ZombieEnemy newObject() {
            return new ZombieEnemy();
        }
    };

    public static void create(PoolEngine poolEngine, Vector2 pos) {
        ZombieEnemy enemy = zombiePool.obtain();
        enemy.init(pos);
        poolEngine.add(enemy);
    }

    protected void init(Vector2 pos) {
        maxHp = 100;
        maxVel = TILE_SIZE * 2.25f;
        attackDamage = 20;
        super.init(pos);
        loadZombieAnimations(animations);
    }

    @Override
    public void render(SpriteBatch batch, ShapeDrawer shaper, float delta, Player player, Array<Spell> spells) {
        update(delta, player, spells);
        GameHelper.checkEnemyStatus(this, delta);
        if (drawHp && !isDead) {
            hpbar.render(shaper, delta, pos, hp);
        }
        GameHelper.damagedEntityAnimation(this, batch, delta);
        renderZombieAnimations(batch);

    }

    private void renderZombieAnimations(SpriteBatch batch) {
        if ((!isAttacking && !isDead) || (isFrozen && !isDead)) {
            if (vel.x < 0) {
                enemyAnimation(ZOMBIE_ANIM_LEFT, batch, LEFT);
            } else if (vel.x > 0) {
                enemyAnimation(ZOMBIE_ANIM_RIGHT, batch, RIGHT);
            } else if (vel.y > 0) {
                enemyAnimation(ZOMBIE_ANIM_UP, batch, UP);
            } else if (vel.y < 0) {
                enemyAnimation(ZOMBIE_ANIM_DOWN, batch, DOWN);
            } else {
                enemyStand(batch);
            }
        } else if (isAttacking && !isDead) {
            enemyAttackingAnimation(batch);

        } else {
            GameHelper.drawEntity(batch, animations.get(ZOMBIE_DEAD_ANIM).getKeyFrame(stateTime), pos, size);

        }
    }

    private void enemyAttackingAnimation(SpriteBatch batch) {
        if (lastDir == RIGHT)
            GameHelper.drawEntity(batch, animations.get(ZOMBIE_ATTACK_RIGHT).getKeyFrame(stateTime), pos, size);
        else if (lastDir == LEFT)
            GameHelper.drawEntity(batch, animations.get(ZOMBIE_ATTACK_LEFT).getKeyFrame(stateTime), pos, size);
        else if (lastDir == UP)
            GameHelper.drawEntity(batch, animations.get(ZOMBIE_ATTACK_UP).getKeyFrame(stateTime), pos, size);
        else if (lastDir == DOWN)
            GameHelper.drawEntity(batch, animations.get(ZOMBIE_ATTACK_DOWN).getKeyFrame(stateTime), pos, size);
    }

    private void enemyStand(SpriteBatch batch) {
        if (lastDir == RIGHT)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_RIGHT), pos, size);
        else if (lastDir == LEFT)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_LEFT), pos, size);
        else if (lastDir == UP)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_UP), pos, size);
        else if (lastDir == DOWN)
            GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_DOWN), pos, size);
    }

    private void enemyAnimation(String zombieAnim, SpriteBatch batch, Direction dir) {
        if (isSlowed) {
            animations.get(zombieAnim).setFrameDuration(FRAME_DURATION * 2);
        } else {
            animations.get(zombieAnim).setFrameDuration(FRAME_DURATION);
        }
        GameHelper.drawEntity(batch, animations.get(zombieAnim).getKeyFrame(stateTime), pos, size);
        lastDir = dir;
    }

    private void loadZombieAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(ZOMBIE_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_RIGHT), LOOP));
        animations.put(ZOMBIE_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_LEFT), LOOP));
        animations.put(ZOMBIE_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_UP), LOOP));
        animations.put(ZOMBIE_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_DOWN), LOOP));
        animations.put(ZOMBIE_ATTACK_RIGHT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_RIGHT), LOOP));
        animations.put(ZOMBIE_ATTACK_LEFT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_LEFT), LOOP));
        animations.put(ZOMBIE_ATTACK_UP, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_UP), LOOP));
        animations.put(ZOMBIE_ATTACK_DOWN, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_DOWN), LOOP));
        animations.put(ZOMBIE_DEAD_ANIM, new Animation<>(DEAD_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_DEAD_ANIM), NORMAL));
    }

    @Override
    public void release() {
        zombiePool.free(this);
    }
}
