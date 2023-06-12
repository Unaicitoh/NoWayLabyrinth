package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.unaig.noway.data.Assets;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.Player;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.Constants.*;

public class SpiderEnemy extends Enemy{

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

    protected void init(){
        super.init();
        loadSpiderAnimations(animations);
    }

    @Override
    public void render(SpriteBatch batch, float delta, Player player) {
        playerBounds.set(player.getBounds());
        playerPos.set(player.getPos());
        update(delta);
        renderSpiderAnimations(batch);

    }

    private void renderSpiderAnimations(SpriteBatch batch) {
        if (!attacking) {
            if(vel.x<0) {
                GameHelper.drawEntity(batch,animations.get(SPIDER_ANIM_LEFT).getKeyFrame(stateTime),pos,size);
                lastDir= Direction.LEFT;
            }else if(vel.x>0) {
                GameHelper.drawEntity(batch,animations.get(SPIDER_ANIM_RIGHT).getKeyFrame(stateTime),pos,size);
                lastDir= Direction.RIGHT;

            }else if(vel.y>0) {
                GameHelper.drawEntity(batch,animations.get(SPIDER_ANIM_UP).getKeyFrame(stateTime),pos,size);
                lastDir= Direction.UP;

            }else if(vel.y<0) {
                GameHelper.drawEntity(batch,animations.get(SPIDER_ANIM_DOWN).getKeyFrame(stateTime),pos,size);
                lastDir= Direction.DOWN;

            }else {
                if(lastDir==Direction.RIGHT) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_RIGHT, 0),pos,size);
                else if (lastDir==Direction.LEFT) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_LEFT, 0),pos,size);
                else if (lastDir==Direction.UP) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_UP, 0),pos,size);
                else if (lastDir==Direction.DOWN) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_DOWN, 0),pos,size);
            }
        } else {
            if(lastDir==Direction.RIGHT) GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_RIGHT).getKeyFrame(stateTime),pos,size);
            else if(lastDir==Direction.LEFT) GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_LEFT).getKeyFrame(stateTime),pos,size);
            else if(lastDir==Direction.UP) GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_UP).getKeyFrame(stateTime),pos,size);
            else if(lastDir==Direction.DOWN) GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_DOWN).getKeyFrame(stateTime),pos,size);

        }
    }

    private void loadSpiderAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(SPIDER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_RIGHT), PlayMode.LOOP));
        animations.put(SPIDER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_LEFT), PlayMode.LOOP));
        animations.put(SPIDER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_UP), PlayMode.LOOP));
        animations.put(SPIDER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_DOWN), PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_RIGHT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_RIGHT), PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_LEFT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_LEFT), PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_UP, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_UP), PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_DOWN, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_DOWN), PlayMode.LOOP));
        animations.put(SPIDER_DEAD_ANIM, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_DEAD_ANIM), PlayMode.NORMAL));
    }
    @Override
    public void release() {
        spiderPool.free(this);
    }
}
