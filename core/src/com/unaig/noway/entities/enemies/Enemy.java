package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.unaig.noway.entities.Entity;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.spells.Spell;
import com.unaig.noway.util.GameHelper;
import com.unaig.noway.util.HPBar;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.unaig.noway.util.Constants.TILE_SIZE;
import static com.unaig.noway.util.Direction.*;

public abstract class Enemy extends Entity implements Poolable {

    public static final String TAG = Enemy.class.getName();

    protected static final float FRAME_DURATION = .09f;
    protected static final float ATTACK_FRAME_DURATION = .15f;
    protected static final float DEAD_FRAME_DURATION = .30f;

    private final float OFFSET_X = 2f;
    private final float OFFSET_Y = 2f;
    public boolean isAlive;
    private float attackRange;
    protected Vector2 playerPos;
    protected Rectangle playerBounds;
    protected boolean attacking;
    private float attackCooldown;
    private float patrolCooldown;
    private Vector2 lastPatrolVel;
    protected HPBar hpbar;
    protected boolean drawHp;
    private float timeFromDamage;
    private float timeToDie;


    protected void init() {
        pos = new Vector2(TILE_SIZE * 2, TILE_SIZE * 2);
        vel = new Vector2(0, 0);
        size = new Vector2(TILE_SIZE, TILE_SIZE);
        maxVel = TILE_SIZE * 2.5f;
        bounds = new Rectangle(pos.x + OFFSET_X, pos.y + OFFSET_Y, size.x - OFFSET_X * 2, size.y - OFFSET_Y * 2);
        int rndDir = MathUtils.random(3);
        switch (rndDir) {
            case 0:
                lastDir = DOWN;
                break;
            case 1:
                lastDir = LEFT;
                break;
            case 2:
                lastDir = RIGHT;
                break;
            case 3:
                lastDir = UP;
                break;
        }

        animations = new ObjectMap<>();
        stateTime = 0f;
        attackRange = TILE_SIZE * 4;
        playerBounds = new Rectangle();
        playerPos = new Vector2();
        attacking = false;
        attackCooldown = 0f;
        patrolCooldown = 1.6f;
        lastPatrolVel = new Vector2();
        isAlive = true;
        drawHp = false;
        hp = maxHp;
        timeFromDamage = 0f;
        timeDamageTaken = .2f;
        isDamaged=false;
        isDead = false;
        timeToDie = 5f;
        hpbar = new HPBar(maxHp, size);
    }

    public abstract void render(SpriteBatch batch, ShapeDrawer shaper, float delta, Player player, Array<Spell> spells);

    public void update(float delta, Player player, Array<Spell> spells) {
        stateTime += delta;
        attackCooldown -= delta;
        patrolCooldown -= delta;
        timeFromDamage += delta;
        if (isDead) {
            timeToDie -= delta;
            if (timeToDie <= 0f) {
                isAlive = false;
            }
        }
        playerBounds.set(player.getBounds());
        playerPos.set(player.getPos());
        vel.x = MathUtils.clamp(vel.x, -maxVel, maxVel);
        vel.y = MathUtils.clamp(vel.y, -maxVel, maxVel);
        checkHitFromSpell(spells);
        if (hpbar.getVisualHp() <= 0 && !isDead) {
            isDead = true;
            stateTime = 0f;
        }
        if (timeFromDamage > 10f) {
            hp = Math.min(maxHp, hp + delta * 10);
        }
        if (!isDead) {
            if ((isPlayerInRange() || hp < maxHp)) {
                drawHp = true;
                if (bounds.overlaps(playerBounds)) {
                    attackPlayer(player);
                } else {
                    chaseMode(delta);
                }
            } else {
                drawHp = false;
                patrolMode(delta);
            }
        }

    }

    private void checkHitFromSpell(Array<Spell> spells) {
        for (Spell s : spells) {
            if (bounds.overlaps(s.getBounds()) && !isDead) {
                s.isAlive = false;
                setIsDamaged(true);
                timeFromDamage = 0;
                hp -= s.getDamage();
                if (hp <= 0f) {
                    hp = 0f;
                }
            }
        }
    }

    private void patrolMode(float delta) {
        float x = Math.round(MathUtils.random(-maxVel, maxVel));
        float y = Math.round(MathUtils.random(-maxVel, maxVel));
        int rnd = MathUtils.random(9);
        if (patrolCooldown <= 0f) {
            patrolCooldown = 1.75f;
            if (x < 0) {
                vel.x = -maxVel;
            } else if (x > 0) {
                vel.x = maxVel;
            } else {
                vel.x = 0;
            }
            if (y < 0) {
                vel.y = -maxVel;
            } else if (y > 0) {
                vel.y = maxVel;
            } else {
                vel.y = 0;
            }
            if (rnd > 8) {
                vel.set(0, 0);
            } else if (rnd > 7) {
                vel.set(0, maxVel);
            } else if (rnd > 6) {
                vel.set(0, -maxVel);
            }
            lastPatrolVel.set(vel);
        }
        checkWallCollisions(delta, lastPatrolVel, true);

    }

    private void attackPlayer(Player player) {
        attacking = true;
        if (attackCooldown <= 0f) {
            attackCooldown = 2f;
            player.setHp(Math.max(0, player.getHp() - attackDamage));
            player.setIsDamaged(true);
        }
    }

    private boolean isPlayerInRange() {
        return pos.dst(playerPos) < attackRange;
    }

    private void chaseMode(float delta) {
        attacking = false;
        Vector2 direction = playerPos.sub(pos);
        if (Math.round(direction.x) > 0) {
            vel.x = maxVel;
        } else if (Math.round(direction.x) < 0) {
            vel.x = -maxVel;
        } else {
            vel.x = 0;
        }
        if (Math.round(direction.y) > 0) {
            vel.y = maxVel;
        } else if (Math.round(direction.y) < 0) {
            vel.y = -maxVel;
        } else {
            vel.y = 0;
        }
        checkWallCollisions(delta, direction, false);

    }

    private void checkWallCollisions(float delta, Vector2 direction, boolean patrolling) {
        Vector2 lastValidPos = new Vector2(pos);
        int directionX = Math.round(direction.x);
        Vector2 sclDir = new Vector2(directionX, 0).nor();
        if (!patrolling) {
            pos.add(sclDir.scl(maxVel * delta));
        } else {
            pos.add(sclDir.scl(maxVel / 2.5f * delta));
        }
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
        if (GameHelper.checkCollisions(bounds)) {
            pos.x = lastValidPos.x;
        } else {
            lastValidPos.x = pos.x;
        }

        int directionY = Math.round(direction.y);
        sclDir.set(0, directionY).nor();
        if (!patrolling) {
            pos.add(sclDir.scl(maxVel * delta));
        } else {
            pos.add(sclDir.scl(maxVel / 2.5f * delta));
        }
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
        if (GameHelper.checkCollisions(bounds)) {
            pos.y = lastValidPos.y;
        } else {
            lastValidPos.y = pos.y;
        }
        pos.set(lastValidPos);
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
    }

    @Override
    public void reset() {
        isAlive = true;
        pos = new Vector2();
        vel = new Vector2();
        size = new Vector2();
        animations = new ObjectMap<>();
        attackRange = 0;
        bounds = new Rectangle();
        maxVel = 0;
        stateTime = 0;
        playerBounds = new Rectangle();
        playerPos = new Vector2();
        attacking = false;
        attackCooldown = 0;
        patrolCooldown = 0;
        hpbar = null;
        hp = 0;
        drawHp = false;
        timeFromDamage = 0f;
        isDead = false;
        timeToDie = 0f;
        maxHp = 0;
    }

    public abstract void release();
}
