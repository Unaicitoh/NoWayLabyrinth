package com.unaig.noway.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.unaig.noway.data.Assets;
import com.unaig.noway.util.Constants;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

public class SpiderEnemy extends Entity{


    private static final float FRAME_DURATION = .1f;
    private PoolEngine poolEngine;

    public SpiderEnemy(PoolEngine poolEngine) {
        this.poolEngine = poolEngine;
        init();
    }

    protected void init() {
        pos = new Vector2(0,0);
        vel = new Vector2(0,0);
        size = new Vector2(Constants.TILE_SIZE, Constants.TILE_SIZE);
        maxVel = Constants.TILE_SIZE*3;
        bounds = new Rectangle(pos.x, pos.y, size.x, size.y);
        lastDir= Direction.RIGHT;
        animations=new ObjectMap<>();
        loadSpiderAnimations(animations);
        stateTime=0f;
    }

    private void loadSpiderAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(Constants.SPIDER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ANIM_RIGHT), Animation.PlayMode.LOOP));
        animations.put(Constants.SPIDER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ANIM_LEFT), Animation.PlayMode.LOOP));
        animations.put(Constants.SPIDER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ANIM_UP), Animation.PlayMode.LOOP));
        animations.put(Constants.SPIDER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ANIM_DOWN), Animation.PlayMode.LOOP));
        animations.put(Constants.SPIDER_ATTACK_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ATTACK_RIGHT), Animation.PlayMode.NORMAL));
        animations.put(Constants.SPIDER_ATTACK_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ATTACK_LEFT), Animation.PlayMode.NORMAL));
        animations.put(Constants.SPIDER_ATTACK_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ATTACK_UP), Animation.PlayMode.NORMAL));
        animations.put(Constants.SPIDER_ATTACK_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_ATTACK_DOWN), Animation.PlayMode.NORMAL));
        animations.put(Constants.SPIDER_DEAD_ANIM, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(Constants.SPIDER_DEAD_ANIM), Animation.PlayMode.NORMAL));
    }

    public void render(SpriteBatch batch, float delta) {
        update(delta);

        if(vel.x<0) {
            GameHelper.drawEntity(batch,animations.get(Constants.SPIDER_ANIM_LEFT).getKeyFrame(stateTime),pos,size);
            lastDir= Direction.LEFT;
        }else if(vel.x>0) {
            GameHelper.drawEntity(batch,animations.get(Constants.SPIDER_ANIM_RIGHT).getKeyFrame(stateTime),pos,size);
            lastDir= Direction.RIGHT;

        }else if(vel.y>0) {
            GameHelper.drawEntity(batch,animations.get(Constants.SPIDER_ANIM_UP).getKeyFrame(stateTime),pos,size);
            lastDir= Direction.UP;

        }else if(vel.y<0) {
            GameHelper.drawEntity(batch,animations.get(Constants.SPIDER_ANIM_DOWN).getKeyFrame(stateTime),pos,size);
            lastDir= Direction.DOWN;

        }else {
            if(lastDir==Direction.RIGHT) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(Constants.SPIDER_ATTACK_RIGHT, 0),pos,size);
            else if (lastDir==Direction.LEFT) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(Constants.SPIDER_ATTACK_LEFT, 0),pos,size);
            else if (lastDir==Direction.UP) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(Constants.SPIDER_ATTACK_UP, 0),pos,size);
            else if (lastDir==Direction.DOWN) GameHelper.drawEntity(batch,Assets.instance.enemiesAtlas.findRegion(Constants.SPIDER_ATTACK_DOWN, 0),pos,size);
        }
    }

    public void update(float delta) {
        stateTime+=delta;
    }
}
