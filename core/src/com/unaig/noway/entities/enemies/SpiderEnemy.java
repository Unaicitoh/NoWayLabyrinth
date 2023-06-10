package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.Entity;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.PoolEngine;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.Constants.TILE_SIZE;

public class SpiderEnemy extends Entity {

    public static final String TAG = Player.class.getName();

    private static final float FRAME_DURATION = .1f;
    private PoolEngine poolEngine;
    private float attackRange;
    private Vector2 playerPos;
    public SpiderEnemy(PoolEngine poolEngine) {
        this.poolEngine = poolEngine;
        init();
    }

    protected void init() {
        pos = new Vector2(TILE_SIZE, TILE_SIZE);
        vel = new Vector2(0,0);
        size = new Vector2(TILE_SIZE, TILE_SIZE);
        maxVel = TILE_SIZE*3;
        bounds = new Rectangle(pos.x, pos.y, size.x, size.y);
        lastDir= Direction.RIGHT;
        animations=new ObjectMap<>();
        stateTime=0f;
        attackRange=TILE_SIZE*5;
        playerPos=new Vector2();
        loadSpiderAnimations(animations);
    }

    private void loadSpiderAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(SPIDER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_RIGHT), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_LEFT), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_UP), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_DOWN), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_RIGHT), Animation.PlayMode.NORMAL));
        animations.put(SPIDER_ATTACK_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_LEFT), Animation.PlayMode.NORMAL));
        animations.put(SPIDER_ATTACK_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_UP), Animation.PlayMode.NORMAL));
        animations.put(SPIDER_ATTACK_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_DOWN), Animation.PlayMode.NORMAL));
        animations.put(SPIDER_DEAD_ANIM, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_DEAD_ANIM), Animation.PlayMode.NORMAL));
    }

    public void render(SpriteBatch batch, float delta, Vector2 playerPos) {
        this.playerPos.set(playerPos);
        update(delta);

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
    }

    public void update(float delta) {
        stateTime+=delta;
        if(isPlayerInRange()){
            chaseMode(delta);
        }
    }

    private boolean isPlayerInRange() {
        return pos.dst(playerPos)<attackRange;
    }

    private void chaseMode(float delta) {
        vel.x= MathUtils.clamp(vel.x,-maxVel,maxVel);
        vel.y= MathUtils.clamp(vel.y,-maxVel,maxVel);
        Vector2 direction=playerPos.sub(pos).nor();
        if(direction.x>0) vel.x=1;
        if(direction.y>0) vel.y=1;
        pos.add(direction.scl(maxVel*delta));
        Gdx.app.log(TAG,"Chasing player");
    }
}
