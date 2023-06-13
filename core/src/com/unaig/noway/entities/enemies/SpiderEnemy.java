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
        super.init();
        loadSpiderAnimations(animations);
    }

    @Override
    public void render(SpriteBatch batch, ShapeDrawer shaper, float delta, Player player, Array<Spell> spells) {
        update(delta, player, spells);
        if (drawHp && !isDead) {
            hpbar.render(shaper, delta, pos, hp);
        }
        renderSpiderAnimations(batch);

    }

    private void renderSpiderAnimations(SpriteBatch batch) {
        if (!attacking && !isDead) {
            if (vel.x < 0) {
                GameHelper.drawEntity(batch, animations.get(SPIDER_ANIM_LEFT).getKeyFrame(stateTime), pos, size);
                lastDir = Direction.LEFT;
            } else if (vel.x > 0) {
                GameHelper.drawEntity(batch, animations.get(SPIDER_ANIM_RIGHT).getKeyFrame(stateTime), pos, size);
                lastDir = Direction.RIGHT;

            } else if (vel.y > 0) {
                GameHelper.drawEntity(batch, animations.get(SPIDER_ANIM_UP).getKeyFrame(stateTime), pos, size);
                lastDir = Direction.UP;

            } else if (vel.y < 0) {
                GameHelper.drawEntity(batch, animations.get(SPIDER_ANIM_DOWN).getKeyFrame(stateTime), pos, size);
                lastDir = Direction.DOWN;

            } else {
                if (lastDir == Direction.RIGHT)
                    GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_RIGHT, 0), pos, size);
                else if (lastDir == Direction.LEFT)
                    GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_LEFT, 0), pos, size);
                else if (lastDir == Direction.UP)
                    GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_UP, 0), pos, size);
                else if (lastDir == Direction.DOWN)
                    GameHelper.drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_DOWN, 0), pos, size);
            }
        } else if (attacking && !isDead) {
            if (lastDir == Direction.RIGHT)
                GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_RIGHT).getKeyFrame(stateTime), pos, size);
            else if (lastDir == Direction.LEFT)
                GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_LEFT).getKeyFrame(stateTime), pos, size);
            else if (lastDir == Direction.UP)
                GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_UP).getKeyFrame(stateTime), pos, size);
            else if (lastDir == Direction.DOWN)
                GameHelper.drawEntity(batch, animations.get(SPIDER_ATTACK_DOWN).getKeyFrame(stateTime), pos, size);

        } else {
            GameHelper.drawEntity(batch, animations.get(SPIDER_DEAD_ANIM).getKeyFrame(stateTime), pos, size);

        }
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