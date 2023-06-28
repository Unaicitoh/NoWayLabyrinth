package com.unaig.noway.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.unaig.noway.data.Assets;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.objects.Item;
import com.unaig.noway.entities.spells.FireSpell;
import com.unaig.noway.entities.spells.IceSpell;
import com.unaig.noway.screens.GameScreen;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.ElementType;
import com.unaig.noway.util.GameHelper;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.unaig.noway.util.AttackType.BASIC;
import static com.unaig.noway.util.AttackType.STRONG;
import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.Direction.DOWN;
import static com.unaig.noway.util.Direction.UP;
import static com.unaig.noway.util.ElementType.FIRE;
import static com.unaig.noway.util.ElementType.ICE;

public class Player extends Entity implements InputProcessor {
    public static final String TAG = Player.class.getName();

    public static final float BASIC_ATTACK_COOLDOWN = .8f;
    public static final float STRONG_ATTACK_COOLDOWN = 2.5f;
    private static final float OFFSET_X = 2.5f;
    private static final float OFFSET_Y = 2.5f;

    private static final float FRAME_DURATION = 0.1f;
    public static final int STRONG_MANA_COST = 25;
    public static final int BASIC_MANA_COST = 5;
    public static final float ATTACK_ANIMATION_FRAME_RESET = .1f;
    public static final float ARMORED_STATE_DURATION = 5f;
    public static final float TIME_SINCE_RUN = .5f;
    public PoolEngine poolEngine;
    private ElementType elementType;
    private float mp;
    private float maxMp;
    private float fireCooldown;
    private float fire2Cooldown;
    private float iceCooldown;
    private float ice2Cooldown;
    private Array<Array<Item>> items;
    private boolean onArmoredState;
    private float armoredStateDuration;
    public final static int MAX_POTS = 5;

    private boolean isRunning;
    public static boolean underAttack;
    private float timeSinceRun;

    public Player(PoolEngine poolEngine) {
        this.poolEngine = poolEngine;
        init();
    }

    public void init() {
        int rnd = MathUtils.random(1);
        elementType = FIRE;
        size = new Vector2(TILE_SIZE, TILE_SIZE);
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Spawns").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            MapObject mapObject = collisions.get(i);
            if (rnd == 0 && mapObject.getName().equals("PlayerSpawn")) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                pos = new Vector2(rectangle.x - size.x / 2, rectangle.y);
            } else if (rnd == 1 && mapObject.getName().equals("PlayerSpawn2")) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                pos = new Vector2(rectangle.x - size.x / 2, rectangle.y);
            }
        }
        maxHp = 100;
        hp = maxHp;
        isDamaged = false;
        timeDamageTaken = DAMAGE_ANIMATION_TIME;
        maxMp = 100;
        mp = maxMp;
        vel = new Vector2(0, 0);
        maxVel = TILE_SIZE * 3;
        bounds = new Rectangle(pos.x + OFFSET_X, pos.y + OFFSET_Y, size.x - OFFSET_X * 2, size.y - OFFSET_Y * 2);
        lastDir = DOWN;
        animations = new ObjectMap<>();
        stateTime = 0f;
        attackDamage = 1;
        fireCooldown = 0;
        iceCooldown = 0;
        fire2Cooldown = 0;
        ice2Cooldown = 0;
        attackCooldown = 0;
        isAttacking = false;
        isDead = false;
        Array<Item> hpP = new Array<>();
        Array<Item> mpP = new Array<>();
        Array<Item> arP = new Array<>();
        Array<Item> keys = new Array<>();
        items = new Array<>();
        items.add(hpP, mpP, arP, keys);
        onArmoredState = false;
        armoredStateDuration = ARMORED_STATE_DURATION;
        isRunning = false;
        timeSinceRun = TIME_SINCE_RUN;
        loadPlayerAnimations(animations);
    }

    private void loadPlayerAnimations(ObjectMap<String, Animation<TextureAtlas.AtlasRegion>> animations) {
        animations.put(PLAYER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_RIGHT), LOOP));
        animations.put(PLAYER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_LEFT), LOOP));
        animations.put(PLAYER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_UP), LOOP));
        animations.put(PLAYER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(PLAYER_ANIM_DOWN), LOOP));
    }

    public void render(SpriteBatch batch, float delta) {
        if (!GameScreen.gameOver) {
            update(delta);
        } else {
            vel.x = 0;
            vel.y = 0;
        }
        GameHelper.damagedEntityAnimation(this, batch, delta);
        renderPlayerAnimations(batch);
        batch.setColor(Color.WHITE);

    }

    private void renderPlayerAnimations(SpriteBatch batch) {
        if (vel.x < 0 && !isDead) {
            playerAnimation(batch, PLAYER_ANIM_LEFT, Direction.LEFT);
        } else if (vel.x > 0 && !isDead) {
            playerAnimation(batch, PLAYER_ANIM_RIGHT, Direction.RIGHT);

        } else if (vel.y > 0 && !isDead) {
            playerAnimation(batch, PLAYER_ANIM_UP, UP);

        } else if (vel.y < 0 && !isDead) {
            playerAnimation(batch, PLAYER_ANIM_DOWN, DOWN);

        } else {
            playerStand(batch);
        }
    }

    private void playerStand(SpriteBatch batch) {
        if (!isAttacking) {
            if (lastDir == Direction.RIGHT)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_RIGHT, 0), pos, size);
            else if (lastDir == Direction.LEFT)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_LEFT, 0), pos, size);
            else if (lastDir == UP)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_UP, 0), pos, size);
            else if (lastDir == DOWN)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_DOWN, 0), pos, size);
        } else {
            if (lastDir == Direction.RIGHT)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_RIGHT, 2), pos, size);
            else if (lastDir == Direction.LEFT)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_LEFT, 2), pos, size);
            else if (lastDir == UP)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_UP, 2), pos, size);
            else if (lastDir == DOWN)
                GameHelper.drawEntity(batch, Assets.instance.playerAtlas.findRegion(PLAYER_ANIM_DOWN, 2), pos, size);
            if (attackCooldown > .25f) {
                isAttacking = false;
                attackCooldown = 0;
            }
        }
    }

    private void playerAnimation(SpriteBatch batch, String playerAnimDir, Direction dir) {
        if (isRunning && !underAttack) {
            animations.get(playerAnimDir).setFrameDuration(FRAME_DURATION / 1.5f);
        } else {
            animations.get(playerAnimDir).setFrameDuration(FRAME_DURATION);
        }
        GameHelper.drawEntity(batch, animations.get(playerAnimDir).getKeyFrame(stateTime), pos, size);
        lastDir = dir;
    }

    private void update(float delta) {
        stateTime += delta;
        vel.x = MathUtils.clamp(vel.x, -maxVel, maxVel);
        vel.y = MathUtils.clamp(vel.y, -maxVel, maxVel);
        if (isRunning && !underAttack && !GameScreen.debugControl) {
            timeSinceRun = 0f;
            if (vel.x > 0) {
                vel.x = TILE_SIZE * 4.25f;
            } else if (vel.x < 0) {
                vel.x = -TILE_SIZE * 4.25f;
            }
            if (vel.y > 0) {
                vel.y = TILE_SIZE * 4.25f;
            } else if (vel.y < 0) {
                vel.y = -TILE_SIZE * 4.25f;
            }
        } else if (!GameScreen.debugControl) {
            timeSinceRun = Math.min(TIME_SINCE_RUN, timeSinceRun + delta);
            maxVel = TILE_SIZE * 3f;

        } else {
            if (vel.x > 0) {
                vel.x = TILE_SIZE * 10;
            } else if (vel.x < 0) {
                vel.x = -TILE_SIZE * 10;
            }
            if (vel.y > 0) {
                vel.y = TILE_SIZE * 10;
            } else if (vel.y < 0) {
                vel.y = -TILE_SIZE * 10;
            }
        }
        mp = Math.min(maxMp, mp + delta * 5);
        hp = Math.min(maxHp, hp);
        updateCooldowns(delta);

        if (hp <= 0) {
            isDead = true;
            GameScreen.gameOver = true;
        }
        checkWallCollisions(delta);
    }

    private void checkWallCollisions(float delta) {
        Vector2 lastValidPos = new Vector2(pos);
        // Move horizontally
        pos.x += vel.x * delta;
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
        if (GameHelper.checkCollisions(bounds) && !GameScreen.debugControl) {
            pos.x = lastValidPos.x;
        } else {
            lastValidPos.x = pos.x;
        }

        // Move vertically
        pos.y += vel.y * delta;
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
        if (GameHelper.checkCollisions(bounds) && !GameScreen.debugControl) {
            pos.y = lastValidPos.y;
        } else {
            lastValidPos.y = pos.y;
        }
        pos.set(lastValidPos);
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
    }

    private void updateCooldowns(float delta) {
        if (isAttacking) attackCooldown += delta;
        if (onArmoredState) {
            armoredStateDuration -= delta;
        }
        if (armoredStateDuration < 0) {
            onArmoredState = false;
            armoredStateDuration = ARMORED_STATE_DURATION;
        }
        fireCooldown -= delta;
        fire2Cooldown -= delta;
        iceCooldown -= delta;
        ice2Cooldown -= delta;

    }

    public void useItem(int currentIndex) {
        switch (currentIndex) {
            case 0:
                if (getItems().get(currentIndex).size == 0) {
                } else {
                    hp += 50;
                    getItems().get(currentIndex).removeIndex(0);
                }
                break;
            case 1:
                if (getItems().get(currentIndex).size == 0) {
                } else {
                    mp += 70;
                    getItems().get(currentIndex).removeIndex(0);
                }
                break;
            case 2:
                if (getItems().get(currentIndex).size == 0) {
                } else {
                    onArmoredState = true;
                    getItems().get(currentIndex).removeIndex(0);
                }
                break;
        }
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
            case Keys.SHIFT_LEFT:
                Gdx.app.log(TAG, "running");
                isRunning = true;
                break;
        }
        return false;
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
            case Keys.SHIFT_LEFT:
                Gdx.app.log(TAG, "stop running");

                isRunning = false;
                break;
        }
        return false;
    }

    public void changeElement() {
        if (elementType == FIRE) {
            elementType = ICE;
        } else if (elementType == ICE) {
            elementType = FIRE;
        }
    }

    public void setMp(float mp) {
        this.mp = mp;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public float getFireCooldown() {
        return fireCooldown;
    }

    public void setFireCooldown(float fireCooldown) {
        this.fireCooldown = fireCooldown;
    }

    public float getFire2Cooldown() {
        return fire2Cooldown;
    }

    public void setFire2Cooldown(float fire2Cooldown) {
        this.fire2Cooldown = fire2Cooldown;
    }

    public float getIceCooldown() {
        return iceCooldown;
    }

    public void setIceCooldown(float iceCooldown) {
        this.iceCooldown = iceCooldown;
    }

    public float getIce2Cooldown() {
        return ice2Cooldown;
    }

    public void setIce2Cooldown(float ice2Cooldown) {
        this.ice2Cooldown = ice2Cooldown;
    }

    public Array<Array<Item>> getItems() {
        return items;
    }

    public void setItems(Array<Array<Item>> items) {
        this.items = items;
    }

    public boolean isOnArmoredState() {
        return onArmoredState;
    }

    public void setOnArmoredState(boolean onArmoredState) {
        this.onArmoredState = onArmoredState;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                if (elementType == FIRE && fireCooldown <= 0f && mp - BASIC_MANA_COST >= 0 && timeSinceRun == TIME_SINCE_RUN) {
                    mp -= BASIC_MANA_COST;
                    stateTime = ATTACK_ANIMATION_FRAME_RESET;
                    fireCooldown = BASIC_ATTACK_COOLDOWN;
                    isAttacking = true;
                    FireSpell.create(poolEngine, this, BASIC);
                } else if (elementType == ICE && iceCooldown <= 0f && mp - BASIC_MANA_COST >= 0 && timeSinceRun == TIME_SINCE_RUN) {
                    mp -= BASIC_MANA_COST;
                    stateTime = ATTACK_ANIMATION_FRAME_RESET;
                    iceCooldown = BASIC_ATTACK_COOLDOWN * 1.5f;
                    isAttacking = true;
                    IceSpell.create(poolEngine, this, BASIC);
                }
                break;
            case Input.Buttons.RIGHT:
                if (elementType == FIRE && fire2Cooldown <= 0f && mp - STRONG_MANA_COST >= 0 && timeSinceRun == TIME_SINCE_RUN) {
                    mp -= STRONG_MANA_COST;
                    stateTime = ATTACK_ANIMATION_FRAME_RESET;
                    fire2Cooldown = STRONG_ATTACK_COOLDOWN;
                    isAttacking = true;
                    FireSpell.create(poolEngine, this, STRONG);
                } else if (elementType == ICE && ice2Cooldown <= 0f && mp - STRONG_MANA_COST >= 0 && timeSinceRun == TIME_SINCE_RUN) {
                    mp -= STRONG_MANA_COST;
                    mp = MathUtils.clamp(mp, 0, maxMp);
                    stateTime = ATTACK_ANIMATION_FRAME_RESET;
                    ice2Cooldown = STRONG_ATTACK_COOLDOWN;
                    isAttacking = true;
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
