package com.unaig.noway.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.Entity;
import com.unaig.noway.entities.enemies.Enemy;
import com.unaig.noway.entities.enemies.SpiderEnemy;
import com.unaig.noway.entities.enemies.ZombieEnemy;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.unaig.noway.util.Constants.*;

public class GameHelper {

    public static boolean checkCollisions(Rectangle r) {
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Collisions").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            MapObject mapObject = collisions.get(i);
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                if (r.overlaps(rectangle)) {
                    return true;
                }

            } else if (mapObject instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) mapObject).getPolygon();
                if (r.overlaps(polygon.getBoundingRectangle())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Animation<AtlasRegion> setAnimation(float duration, String animationName) {
        return new Animation<>(duration, Assets.instance.playerAtlas.findRegions(animationName), LOOP);
    }

    public static void drawEntity(SpriteBatch batch, AtlasRegion atlasRegion, Vector2 pos, Vector2 size) {
        batch.draw(atlasRegion, pos.x, pos.y, size.x, size.y);
    }

    public static void damagedEntityAnimation(Entity e, SpriteBatch batch, float delta) {
        if (e.getIsDamaged() && e.timeDamageTaken >= 0) {
            e.timeDamageTaken -= delta;
            batch.setColor(1, 0, 0, e.timeDamageTaken * 5);
        } else {
            e.setIsDamaged(false);
            e.timeDamageTaken = DAMAGE_ANIMATION_TIME;
            batch.setColor(1, 1, 1, 1);
        }
    }

    public static void checkEnemyStatus(Enemy enemy, float delta) {
        if (enemy.isBurned() && enemy.getBurnDuration() >= 0) {
            updateBurnStatus(enemy, delta);

        } else if (enemy.isFrozen() && enemy.getFrozenDuration() >= 0) {
            updateFrozenStatus(enemy, delta);
        } else if (enemy.isSlowed() && enemy.getSlowedDuration() >= 0) {
            updateSlowedStatus(enemy, delta);
        } else {
            if (enemy instanceof SpiderEnemy) enemy.maxVel = TILE_SIZE * 2.75f;
            else if (enemy instanceof ZombieEnemy) enemy.maxVel = TILE_SIZE * 2.25f;
        }
    }

    private static void updateSlowedStatus(Enemy enemy, float delta) {
        enemy.setSlowedDuration(enemy.getSlowedDuration() - delta);
        if (enemy.getSlowedDuration() < 0) {
            enemy.setSlowedDuration(SLOWED_ENEMY_TIME);
            enemy.setSlowed(false);
            if (enemy instanceof SpiderEnemy) enemy.maxVel = TILE_SIZE * 2.75f;
            else if (enemy instanceof ZombieEnemy) enemy.maxVel = TILE_SIZE * 2.25f;
        }
    }

    private static void updateFrozenStatus(Enemy enemy, float delta) {
        enemy.setFrozenDuration(enemy.getFrozenDuration() - delta);
        enemy.maxVel = 0;
        if (enemy.getFrozenDuration() < 0) {
            enemy.setFrozenDuration(FROZEN_ENEMY_TIME);
            enemy.setFrozen(false);
            if (enemy instanceof SpiderEnemy) enemy.maxVel = TILE_SIZE * 2.75f;
            else if (enemy instanceof ZombieEnemy) enemy.maxVel = TILE_SIZE * 2.25f;
        }
    }

    private static void updateBurnStatus(Enemy enemy, float delta) {
        enemy.setBurnDuration(enemy.getBurnDuration() - delta);
        enemy.setHp(enemy.getHp() - delta * 5);
        if (enemy.isSlowed()) {
            if (enemy instanceof SpiderEnemy) enemy.maxVel = TILE_SIZE * 2.75f;
            else if (enemy instanceof ZombieEnemy) enemy.maxVel = TILE_SIZE * 2.25f;
            enemy.setSlowed(false);
        }
        if (enemy.getBurnDuration() < 0) {
            enemy.setBurned(false);
            enemy.setBurnDuration(BURN_ENEMY_TIME);
        }
    }

    private GameHelper() {
    }

}
