package com.unaig.noway.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.unaig.noway.data.Assets;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.spells.FireSpell;
import com.unaig.noway.entities.spells.IceSpell;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.ElementType;
import com.unaig.noway.util.GameHelper;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.unaig.noway.util.AttackType.BASIC;
import static com.unaig.noway.util.AttackType.STRONG;
import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.ElementType.FIRE;
import static com.unaig.noway.util.ElementType.ICE;

public class Player extends Entity implements InputProcessor {

    public static final String TAG = Player.class.getName();

    public PoolEngine poolEngine;
    private ElementType elementType;
    private float mp;
    private float maxMp;
    private float fireCooldown;
    private float fire2Cooldown;
    private float iceCooldown;
    private float ice2Cooldown;
    public static final float BASIC_ATTACK_COOLDOWN = .5f;
    public static final float STRONG_ATTACK_COOLDOWN = 2f;
    private static final float OFFSET_X = 2f;
    private static final float FRAME_DURATION = 0.1f;

    public Player(PoolEngine poolEngine) {
        this.poolEngine = poolEngine;
        init();
    }

    protected void init() {
//		int rnd = MathUtils.random(1);
        int rnd = 0;
        elementType = FIRE;
        size = new Vector2(TILE_SIZE, TILE_SIZE);
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Spawns").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            MapObject mapObject = collisions.get(i);

            if (rnd == 0) {
                if (mapObject.getName().equals("PlayerSpawn")) {
                    Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                    pos = new Vector2(rectangle.x - size.x / 2, rectangle.y);
                }
            } else {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                pos = new Vector2(rectangle.x - size.x / 2, rectangle.y);
            }
        }
        maxHp = 100;
        hp = maxHp;
        maxMp = 100;
        mp = maxMp;
        vel = new Vector2(0, 0);
        maxVel = TILE_SIZE * 3;
        bounds = new Rectangle(pos.x + OFFSET_X, pos.y, size.x - OFFSET_X * 2, size.y);
        lastDir = Direction.DOWN;
        animations = new ObjectMap<>();
        stateTime = 0f;
        attackDamage = 1;
        fireCooldown = 0;
        iceCooldown = 0;
        fire2Cooldown = 0;
        ice2Cooldown = 0;
        loadPlayerAnimations(animations);
    }

    private void loadPlayerAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(PLAYER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_RIGHT), LOOP));
        animations.put(PLAYER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_LEFT), LOOP));
        animations.put(PLAYER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_UP), LOOP));
        animations.put(PLAYER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_DOWN), LOOP));
    }

    public void render(SpriteBatch batch, float delta) {
        update(delta);
        Gdx.app.log(TAG, "player hp: " + hp);
        Gdx.app.log(TAG, "player mp: " + mp);
        renderPlayerAnimations(batch);

    }

    private void renderPlayerAnimations(SpriteBatch batch) {
        if (vel.x < 0) {
            GameHelper.drawEntity(batch, animations.get(PLAYER_ANIM_LEFT).getKeyFrame(stateTime), pos, size);
            lastDir = Direction.LEFT;
        } else if (vel.x > 0) {
            GameHelper.drawEntity(batch, animations.get(PLAYER_ANIM_RIGHT).getKeyFrame(stateTime), pos, size);
            lastDir = Direction.RIGHT;

        } else if (vel.y > 0) {
            GameHelper.drawEntity(batch, animations.get(PLAYER_ANIM_UP).getKeyFrame(stateTime), pos, size);
            lastDir = Direction.UP;

        } else if (vel.y < 0) {
            GameHelper.drawEntity(batch, animations.get(PLAYER_ANIM_DOWN).getKeyFrame(stateTime), pos, size);
            lastDir = Direction.DOWN;

        } else {
            if (lastDir == Direction.RIGHT)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_RIGHT, 0), pos, size);
            else if (lastDir == Direction.LEFT)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_LEFT, 0), pos, size);
            else if (lastDir == Direction.UP)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_UP, 0), pos, size);
            else if (lastDir == Direction.DOWN)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_DOWN, 0), pos, size);
        }
    }

    private void update(float delta) {
        stateTime += delta;
        mp = Math.min(maxMp, mp + delta);
        updateCooldowns(delta);
        vel.x = MathUtils.clamp(vel.x, -maxVel, maxVel);
        vel.y = MathUtils.clamp(vel.y, -maxVel, maxVel);
        checkWallCollisions(delta);
    }

    private void checkWallCollisions(float delta) {
        Vector2 lastValidPos = new Vector2(pos);
        // Move horizontally
        pos.x += vel.x * delta;
        bounds.setPosition(pos.x + OFFSET_X, pos.y);
        if (GameHelper.checkCollisions(bounds)) {
            pos.x = lastValidPos.x;
        } else {
            lastValidPos.x = pos.x;
        }

        // Move vertically
        pos.y += vel.y * delta;
        bounds.setPosition(pos.x + OFFSET_X, pos.y);
        if (GameHelper.checkCollisions(bounds)) {
            pos.y = lastValidPos.y;
        } else {
            lastValidPos.y = pos.y;
        }
        pos.set(lastValidPos);
        bounds.setPosition(pos.x + OFFSET_X, pos.y);
    }

    private void updateCooldowns(float delta) {
        fireCooldown -= delta;
        fire2Cooldown -= delta;
        iceCooldown -= delta;
        ice2Cooldown -= delta;
    }

    public float getMp() {
        return mp;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.D:
                vel.x = maxVel;
                break;
            case Keys.A:
                vel.x = -maxVel;
                break;
            case Keys.W:
                vel.y = maxVel;
                break;
            case Keys.S:
                vel.y = -maxVel;
                break;
            case Keys.SPACE:
                changeElement();
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.D:
                if (vel.x > 0) {
                    vel.x = 0;
                    if (Gdx.input.isKeyPressed(Keys.A)) vel.x = -maxVel;
                    if (Gdx.input.isKeyPressed(Keys.W)) vel.y = maxVel;
                    else if (Gdx.input.isKeyPressed(Keys.S)) vel.y = -maxVel;
                }
                stateTime = 0f;
                break;
            case Keys.A:
                if (vel.x < 0) {
                    vel.x = 0;
                    if (Gdx.input.isKeyPressed(Keys.D)) vel.x = maxVel;
                    if (Gdx.input.isKeyPressed(Keys.W)) vel.y = maxVel;
                    else if (Gdx.input.isKeyPressed(Keys.S)) vel.y = -maxVel;
                }
                stateTime = 0f;
                break;
            case Keys.W:
                if (vel.y > 0) {
                    vel.y = 0;
                    if (Gdx.input.isKeyPressed(Keys.S)) vel.y = -maxVel;
                    if (Gdx.input.isKeyPressed(Keys.D)) vel.x = maxVel;
                    else if (Gdx.input.isKeyPressed(Keys.A)) vel.x = -maxVel;
                }
                stateTime = 0f;
                break;
            case Keys.S:
                if (vel.y < 0) {
                    vel.y = 0;
                    if (Gdx.input.isKeyPressed(Keys.W)) vel.y = maxVel;
                    if (Gdx.input.isKeyPressed(Keys.D)) vel.x = maxVel;
                    else if (Gdx.input.isKeyPressed(Keys.A)) vel.x = -maxVel;
                }
                stateTime = 0f;
                break;
        }
        return true;
    }

    private void changeElement() {
        if (elementType == FIRE) {
            elementType = ICE;
        } else if (elementType == ICE) {
            elementType = FIRE;
        }
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Buttons.LEFT:
                if (elementType == FIRE && fireCooldown <= 0f && mp - 5 >= 0) {
                    mp -= 5;
                    stateTime = 0;
                    fireCooldown = BASIC_ATTACK_COOLDOWN;
                    FireSpell.create(poolEngine, this, BASIC);
                } else if (elementType == ICE && iceCooldown <= 0f && mp - 5 >= 0) {
                    mp -= 5;
                    stateTime = 0;
                    iceCooldown = BASIC_ATTACK_COOLDOWN;
                    IceSpell.create(poolEngine, this, BASIC);
                }
                break;
            case Buttons.RIGHT:
                if (elementType == FIRE && fire2Cooldown <= 0f && mp - 30 >= 0) {
                    mp -= 30;
                    stateTime = 0;
                    fire2Cooldown = STRONG_ATTACK_COOLDOWN;
                    FireSpell.create(poolEngine, this, STRONG);
                } else if (elementType == ICE && ice2Cooldown <= 0f && mp - 30 >= 0) {
                    mp -= 30;
                    mp = MathUtils.clamp(mp, 0, maxMp);
                    stateTime = 0;
                    ice2Cooldown = STRONG_ATTACK_COOLDOWN;
                    IceSpell.create(poolEngine, this, STRONG);
                }
                break;
        }
        mp = Math.max(0, mp);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return false;
    }

}
