package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.Entity;
import com.unaig.noway.entities.Player;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.spells.IceSpell;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.Constants.TILE_SIZE;

public class Enemy extends Entity implements Pool.Poolable {

    public static final String TAG = Player.class.getName();

    private static Pool<Enemy> spiderPool = Pools.get(Enemy.class);

    private static final float FRAME_DURATION = .09f;
    private static final float ATTACK_FRAME_DURATION = .15f;
    private final float OFFSET_X = 2f;
    private final float OFFSET_Y = 2f;
    private float attackRange;
    private Vector2 playerPos;
    private Rectangle playerBounds;
    private boolean attacking;
    private float attackCooldown;

    public static void create(PoolEngine poolEngine) {
        Enemy enemy = spiderPool.obtain();
        enemy.init();
        poolEngine.add(enemy);
    }

    protected void init() {
        pos = new Vector2(TILE_SIZE*2, TILE_SIZE*2);
        vel = new Vector2(0,0);
        size = new Vector2(TILE_SIZE, TILE_SIZE);
        maxVel = TILE_SIZE*2.5f;
        bounds = new Rectangle(pos.x+OFFSET_X, pos.y+OFFSET_Y, size.x-OFFSET_X*2, size.y-OFFSET_Y*2);
        lastDir= Direction.RIGHT;
        animations=new ObjectMap<>();
        stateTime=0f;
        attackRange=TILE_SIZE*4;
        playerBounds = new Rectangle();
        playerPos=new Vector2();
        attacking=false;
        attackCooldown=0f;
        loadSpiderAnimations(animations);
    }

    private void loadSpiderAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(SPIDER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_RIGHT), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_LEFT), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_UP), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_DOWN), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_RIGHT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_RIGHT), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_LEFT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_LEFT), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_UP, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_UP), Animation.PlayMode.LOOP));
        animations.put(SPIDER_ATTACK_DOWN, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_DOWN), Animation.PlayMode.LOOP));
        animations.put(SPIDER_DEAD_ANIM, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_DEAD_ANIM), Animation.PlayMode.NORMAL));
    }

    public void render(SpriteBatch batch, float delta, Player player) {
        playerBounds.set(player.getBounds());
        playerPos.set(player.getPos());
        update(delta);
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
            if(lastDir==Direction.RIGHT) {
                GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_RIGHT).getKeyFrame(stateTime),pos,size);
                if(attackCooldown<=0){
                    damagePlayer();
                }
            } else if(lastDir==Direction.LEFT) GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_LEFT).getKeyFrame(stateTime),pos,size);
            else if(lastDir==Direction.UP) GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_UP).getKeyFrame(stateTime),pos,size);
            else if(lastDir==Direction.DOWN) GameHelper.drawEntity(batch,animations.get(SPIDER_ATTACK_DOWN).getKeyFrame(stateTime),pos,size);

        }
    }

    public void update(float delta) {
        stateTime+=delta;
        if(isPlayerInRange()){
            if(bounds.overlaps(playerBounds)){
                attackPlayer(delta);
            }else{
                chaseMode(delta);
            }
        }else{
            patrolMode();
            vel.x=0;
            vel.y=0;
        }
    }

    private void damagePlayer() {
        attackCooldown=1.5f;
        Gdx.app.log(TAG,"damaging player");
    }

    private void patrolMode() {
    }

    private void attackPlayer(float delta) {
        attacking=true;
        attackCooldown-=delta;
    }

    private boolean isPlayerInRange() {
        return pos.dst(playerPos)<attackRange;
    }

    private void chaseMode(float delta) {
        attacking=false;
        vel.x= MathUtils.clamp(vel.x,-maxVel,maxVel);
        vel.y= MathUtils.clamp(vel.y,-maxVel,maxVel);
        Vector2 direction=playerPos.sub(pos);
        if(Math.round(direction.x)>0){
            vel.x=maxVel;
        } else if (Math.round(direction.x)<0) {
            vel.x=-maxVel;
        }else {
            vel.x = 0;
        }
        if(Math.round(direction.y)>0){
            vel.y=maxVel;
        } else if (Math.round(direction.y)<0) {
            vel.y=-maxVel;
        }else {
            vel.y = 0;
        }
        checkWallCollisions(delta,direction);

    }

    private void checkWallCollisions(float delta,Vector2 direction) {
        Vector2 lastValidPos = new Vector2(pos);
        int directionX = Math.round(direction.x);
        Vector2 sclDir= new Vector2(directionX,0).nor();
        pos.add(sclDir.scl(maxVel* delta));
        bounds.setPosition(pos.x+OFFSET_X,pos.y+OFFSET_Y);
        if(GameHelper.checkCollisions(bounds)){
            pos.x= lastValidPos.x;
        }else{
            lastValidPos.x=pos.x;
        }

        int directionY = Math.round(direction.y);
        sclDir.set(0,directionY).nor();
        pos.add(sclDir.scl(maxVel* delta));
        bounds.setPosition(pos.x+OFFSET_X,pos.y+OFFSET_Y);
        if(GameHelper.checkCollisions(bounds)){
            pos.y= lastValidPos.y;
        }else{
            lastValidPos.y=pos.y;
        }
        pos.set(lastValidPos);
        bounds.setPosition(pos.x+OFFSET_X,pos.y+OFFSET_Y);
    }

    @Override
    public void reset() {

    }
}