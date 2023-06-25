package com.unaig.noway.entities.enemies;

import com.badlogic.gdx.Gdx;
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

public class GhostEnemy extends Enemy {

    private static final Pool<GhostEnemy> ghostPool = new Pool<GhostEnemy>() {
        @Override
        protected GhostEnemy newObject() {
            return new GhostEnemy();
        }
    };
    public static final float RESPAWN_ANIMATION_TIME = .75f;

    private float respawnAnimationTime;

    public static void create(PoolEngine poolEngine, Vector2 pos) {
        GhostEnemy enemy = ghostPool.obtain();
        enemy.init(pos);
        poolEngine.add(enemy);
    }

    protected void init(Vector2 pos) {
        maxHp = 100;
        maxVel = TILE_SIZE * 2.25f;
        attackDamage = 25;
        attackRange = TILE_SIZE * 7f;
        respawnAnimationTime = RESPAWN_ANIMATION_TIME;
        super.init(pos);
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
        if (revertGhost && respawnAnimationTime >= 0) {
            respawnAnimationTime -= delta;
            Gdx.app.log(TAG,"alpha "+respawnAnimationTime);
            batch.setColor(.5f, .5f, .5f, respawnAnimationTime * 2.1f);
            if (respawnAnimationTime < RESPAWN_ANIMATION_TIME * .2f) {
                pos.set(lastValidPos);
            }
        } else {
            revertGhost = false;
            respawnAnimationTime = RESPAWN_ANIMATION_TIME;
            batch.setColor(1, 1, 1, 1);
        }
        GameHelper.renderEnemyAnimations(this, batch);

    }

    @Override
    public void release() {
        ghostPool.free(this);
    }
}
