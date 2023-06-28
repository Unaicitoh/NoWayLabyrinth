package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.spells.Spell;
import com.unaig.noway.util.GameHelper;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.unaig.noway.util.Constants.TILE_SIZE;

public class ZombieEnemy extends Enemy {

    private static final Pool<ZombieEnemy> zombiePool = new Pool<ZombieEnemy>() {
        @Override
        protected ZombieEnemy newObject() {
            return new ZombieEnemy();
        }
    };

    public static void create(PoolEngine poolEngine, Vector2 pos, EnemyListener listener) {
        ZombieEnemy enemy = zombiePool.obtain();
        enemy.init(pos, listener);
        poolEngine.add(enemy);
    }

    protected void init(Vector2 pos, EnemyListener listener) {
        maxHp = 90;
        maxVel = TILE_SIZE * 2.50f;
        attackDamage = 20;
        attackRange = TILE_SIZE * 5.5f;
        super.init(pos, listener);
        GameHelper.loadEnemyAnimations(this, animations);
    }

    @Override
    public void render(SpriteBatch batch, ShapeDrawer shaper, float delta, Player player, Array<Spell> spells) {
        update(delta, player, spells);
        GameHelper.checkEnemyStatus(this, delta);
        if (drawHp && !isDead) {
            hpbar.render(shaper, delta, pos, hp);
        }
        GameHelper.damagedEntityAnimation(this, batch, delta);
        if (!isDamaged) {
            GameHelper.updateEnemyStatus(this, batch);
        }
        GameHelper.renderEnemyAnimations(this, batch);
        batch.setColor(Color.WHITE);

    }

    @Override
    public void release() {
        zombiePool.free(this);
    }
}
