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
import com.unaig.noway.entities.spells.FireSpell;
import com.unaig.noway.entities.spells.IceSpell;
import com.unaig.noway.entities.spells.Spell;
import com.unaig.noway.util.GameHelper;
import com.unaig.noway.util.HPBar;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.unaig.noway.util.AttackType.BASIC;
import static com.unaig.noway.util.AttackType.STRONG;
import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.Direction.*;

public abstract class Enemy extends Entity implements Poolable {

    public static final String TAG = Enemy.class.getName();

    public static final float FRAME_DURATION = .09f;
    public static final float ATTACK_FRAME_DURATION = .15f;
    public static final float DEAD_FRAME_DURATION = .30f;

    private final float OFFSET_X = 3.25f;
    private final float OFFSET_Y = 3.25f;
    public boolean isAlive;
    protected float attackRange;
    protected Vector2 playerPos;
    protected Vector2 lastValidPos;
    protected Rectangle playerBounds;
    private float patrolCooldown;
    private Vector2 lastPatrolVel;
    protected HPBar hpbar;
    protected boolean drawHp;
    private float timeFromDamage;
    private float timeToDie;
    protected boolean isBurned;
    protected boolean isSlowed;
    protected boolean isFrozen;
    protected float burnDuration;
    protected float slowedDuration;
    protected float frozenDuration;
    public Array<Rectangle> lineSight;
    private boolean readyToAttack;
    private boolean isChasing;
    private boolean collide;
    protected boolean revertGhost;

    private EnemyListener listener;

    public interface EnemyListener {
        void onEnemyDead();
    }

    protected void init(Vector2 _pos, EnemyListener listener) {
        pos = new Vector2();
        pos.set(_pos);
        vel = new Vector2(0, 0);
        size = new Vector2(TILE_SIZE, TILE_SIZE);
        bounds = new Rectangle(pos.x + OFFSET_X, pos.y + OFFSET_Y, size.x - OFFSET_X * 2, size.y - OFFSET_Y * 2);
        lastValidPos = new Vector2();
        collide = true;
        revertGhost = false;
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
        playerBounds = new Rectangle();
        playerPos = new Vector2();
        isAttacking = false;
        attackCooldown = 0f;
        patrolCooldown = 1.6f;
        lastPatrolVel = new Vector2();
        isAlive = true;
        drawHp = false;
        hp = maxHp;
        timeFromDamage = 0f;
        timeDamageTaken = .2f;
        isDamaged = false;
        isDead = false;
        timeToDie = 5f;
        hpbar = new HPBar(maxHp, size);
        isBurned = false;
        isSlowed = false;
        isFrozen = false;
        burnDuration = BURN_ENEMY_TIME;
        slowedDuration = SLOWED_ENEMY_TIME;
        frozenDuration = FROZEN_ENEMY_TIME;
        lineSight = new Array<>();
        readyToAttack = false;
        this.listener = listener;
    }

    public abstract void render(SpriteBatch batch, ShapeDrawer shaper, float delta, Player player, Array<Spell> spells);

    public void update(float delta, Player player, Array<Spell> spells) {
        stateTime += delta;
        updateCooldowns(delta);
        playerBounds.set(player.getBounds());
        playerPos.set(player.getPos());
        vel.x = MathUtils.clamp(vel.x, -maxVel, maxVel);
        vel.y = MathUtils.clamp(vel.y, -maxVel, maxVel);
        checkHitFromSpell(spells);
        checkStatus(delta);
        if (timeFromDamage > 10f) {
            hp = Math.min(maxHp, hp + delta * 10);
        }
        if (hp <= 0) {
            vel.x = 0;
            vel.y = 0;
        }
        if (!isDead && !isFrozen && hp > 0) {
            if (isPlayerInRange() || hp < maxHp) {
                drawHp = true;
                readyToAttack = true;
                if (bounds.overlaps(playerBounds)) {
                    attackPlayer(player);
                } else {
                    chaseMode(delta);
                }
            } else {
                drawHp = false;
                readyToAttack = false;
                patrolMode(delta);
            }
        } else if (!isDead) {
            drawHp = true;
        }

    }

    private void updateCooldowns(float delta) {
        attackCooldown -= delta;
        patrolCooldown -= delta;
        timeFromDamage += delta;
    }

    private void checkStatus(float delta) {
        if (isDead) {
            timeToDie -= delta;
            if (timeToDie <= 0f) {
                isAlive = false;
            }
        }

        if (hpbar.getVisualHp() <= 0 && !isDead) {
            isDead = true;
            stateTime = 0f;
            listener.onEnemyDead();
        }
    }

    private void checkHitFromSpell(Array<Spell> spells) {
        for (Spell s : spells) {
            if (bounds.overlaps(s.getBounds()) && hp > 0 && s.isAlive) {
                boolean extraDamage = false;
                s.isAlive = false;
                if (s instanceof FireSpell) {
                    if (s.getAttackType() == BASIC) {
                        if (!isFrozen) {
                            isBurned = true;
                        }
                        slowedDuration = 0.25f;
                        if (isBurned) {
                            burnDuration = BURN_ENEMY_TIME;
                        }
                    } else if (s.getAttackType() == STRONG) {
                        if (isFrozen) {
                            isFrozen = false;
                            frozenDuration = 0;
                            extraDamage = true;
                        } else {
                            isBurned = true;
                            burnDuration = BURN_ENEMY_TIME * 1.5f;
                        }
                    }
                } else if (s instanceof IceSpell) {
                    isBurned = false;
                    if (s.getAttackType() == BASIC) {
                        if (isSlowed) {
                            slowedDuration = SLOWED_ENEMY_TIME;
                        } else {
                            maxVel /= 1.75f;
                            isSlowed = true;
                        }
                    } else if (s.getAttackType() == STRONG) {
                        isFrozen = true;
                    }
                }
                setIsDamaged(true);
                timeFromDamage = 0;
                if (extraDamage) {
                    hp -= s.getDamage() * 1.5;
                } else {
                    hp -= s.getDamage();
                }
                if (hp < 0f) {
                    hp = 0f;
                }
            }
        }
    }

    private void patrolMode(float delta) {
        isChasing = false;

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
        isAttacking = true;
        isChasing = false;

        if (attackCooldown <= 0f) {
            attackCooldown = 2f;
            if (player.isOnArmoredState()) {
                player.setHp(Math.max(0, player.getHp() - attackDamage / 2f));
            } else {
                player.setHp(Math.max(0, player.getHp() - attackDamage));
            }
            player.setIsDamaged(true);
        }
    }

    private boolean isPlayerInRange() {
        if (pos.dst(playerPos) < attackRange) {
            if (readyToAttack) return true;
            else return checkLineSight();
        } else
            return false;
    }

    private boolean checkLineSight() {
        float x0 = playerPos.x + TILE_SIZE / 2f - OFFSET_X;
        float y0 = playerPos.y + TILE_SIZE / 2f;
        float x1 = pos.x + TILE_SIZE / 2f;
        float y1 = pos.y + TILE_SIZE / 2f;
        x0 = Math.round(x0);
        y0 = Math.round(y0);
        x1 = Math.round(x1);
        y1 = Math.round(y1);
        float dx = Math.abs(x1 - x0);
        float dy = Math.abs(y1 - y0);
        float sx = (x0 < x1) ? 1 : -1;
        float sy = (y0 < y1) ? 1 : -1;
        float err = dx - dy;
        boolean onSight = true;
        while (x0 != x1 || y0 != y1) {

            lineSight.add(new Rectangle(x0, y0, 1, 1));

            float e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
        for (Rectangle r : lineSight) {
            if (GameHelper.checkCollisions(r)) {
                onSight = false;
            }
        }
        lineSight.clear();
        return onSight;
    }

    private void chaseMode(float delta) {
        isAttacking = false;
        isChasing = true;
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
        collide = (!isChasing && !isAttacking) || !(this instanceof GhostEnemy);
        if (collide && !patrolling) {
            lastValidPos = new Vector2(pos);
        }
        if (this instanceof GhostEnemy && patrolling && GameHelper.checkCollisions(bounds)) {
            collide = false;
        }
        int directionX = Math.round(direction.x);
        Vector2 sclDir = new Vector2(directionX, 0).nor();
        if (!patrolling) {
            pos.add(sclDir.scl(maxVel * delta));
        } else {
            pos.add(sclDir.scl(maxVel / 2.5f * delta));
        }
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
        if (collide) {
            if (GameHelper.checkCollisions(bounds)) {
                pos.x = lastValidPos.x;
            } else {
                lastValidPos.x = pos.x;
            }
        } else {
            if (!GameHelper.checkCollisions(bounds)) {
                lastValidPos.x = pos.x;
            }
        }


        int directionY = Math.round(direction.y);
        sclDir.set(0, directionY).nor();
        if (!patrolling) {
            pos.add(sclDir.scl(maxVel * delta));
        } else {
            pos.add(sclDir.scl(maxVel / 2.5f * delta));
        }
        bounds.setPosition(pos.x + OFFSET_X, pos.y + OFFSET_Y);
        if (collide) {
            if (GameHelper.checkCollisions(bounds)) {
                pos.y = lastValidPos.y;
            } else {
                lastValidPos.y = pos.y;
            }
        } else {
            if (!GameHelper.checkCollisions(bounds)) {
                lastValidPos.y = pos.y;
            }
        }
        if (collide) {
            pos.set(lastValidPos);
        } else if (patrolling) {
            vel.x = 0;
            vel.y = 0;
            revertGhost = true;
        }
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
        isAttacking = false;
        attackCooldown = 0;
        patrolCooldown = 0;
        hpbar = null;
        hp = 0;
        drawHp = false;
        timeFromDamage = 0f;
        isDead = false;
        timeToDie = 0f;
        maxHp = 0;
        isDamaged = false;
        isBurned = false;
        isSlowed = false;
        isFrozen = false;
        timeDamageTaken = 0f;
        burnDuration = 0f;
        slowedDuration = 0f;
        frozenDuration = 0f;
        lineSight.clear();
        readyToAttack = false;
    }

    public abstract void release();

    public boolean isBurned() {
        return isBurned;
    }

    public void setBurned(boolean burned) {
        isBurned = burned;
    }

    public boolean isSlowed() {
        return isSlowed;
    }

    public void setSlowed(boolean slowed) {
        isSlowed = slowed;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }

    public float getBurnDuration() {
        return burnDuration;
    }

    public void setBurnDuration(float burnDuration) {
        this.burnDuration = burnDuration;
    }

    public float getSlowedDuration() {
        return slowedDuration;
    }

    public void setSlowedDuration(float slowedDuration) {
        this.slowedDuration = slowedDuration;
    }

    public float getFrozenDuration() {
        return frozenDuration;
    }

    public void setFrozenDuration(float frozenDuration) {
        this.frozenDuration = frozenDuration;
    }
}
