package com.unaig.noway.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.utils.ObjectMap;
import com.github.tommyettinger.colorful.rgb.Palette;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.Entity;
import com.unaig.noway.entities.enemies.Enemy;
import com.unaig.noway.entities.enemies.GhostEnemy;
import com.unaig.noway.entities.enemies.SpiderEnemy;
import com.unaig.noway.entities.enemies.ZombieEnemy;
import com.unaig.noway.screens.GameScreen;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL;
import static com.unaig.noway.entities.enemies.Enemy.*;
import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.Direction.*;

public class GameHelper {

    public static boolean checkCollisions(Rectangle r) {
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Collisions").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            MapObject mapObject = collisions.get(i);
            if (GameScreen.barrierPos.overlaps(r) && !GameScreen.onMaxKeys()) {
                return true;
            }
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

    public static boolean checkCollisions(Rectangle r, boolean isSpell) {
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Collisions").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            MapObject mapObject = collisions.get(i);
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                if (r.overlaps(rectangle)) {
                    if (mapObject.getProperties().get("fall") != null) {
                        return !(boolean) mapObject.getProperties().get("fall");
                    } else {
                        return true;
                    }
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
            batch.setColor(.5f, .5f, .5f, e.timeDamageTaken * 5);
        } else {
            e.setIsDamaged(false);
            e.timeDamageTaken = DAMAGE_ANIMATION_TIME;
            batch.setPackedColor(Palette.GRAY);
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
            else if (enemy instanceof GhostEnemy) enemy.maxVel = TILE_SIZE * 2f;
        }
    }

    private static void updateSlowedStatus(Enemy enemy, float delta) {
        enemy.setSlowedDuration(enemy.getSlowedDuration() - delta);
        if (enemy.getSlowedDuration() < 0) {
            enemy.setSlowedDuration(SLOWED_ENEMY_TIME);
            enemy.setSlowed(false);
            if (enemy instanceof SpiderEnemy) enemy.maxVel = TILE_SIZE * 2.75f;
            else if (enemy instanceof ZombieEnemy) enemy.maxVel = TILE_SIZE * 2.25f;
            else if (enemy instanceof GhostEnemy) enemy.maxVel = TILE_SIZE * 2f;
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
            else if (enemy instanceof GhostEnemy) enemy.maxVel = TILE_SIZE * 2f;
        }
    }

    private static void updateBurnStatus(Enemy enemy, float delta) {
        enemy.setBurnDuration(enemy.getBurnDuration() - delta);
        enemy.setHp(enemy.getHp() - delta * 5);
        if (enemy.isSlowed()) {
            if (enemy instanceof SpiderEnemy) enemy.maxVel = TILE_SIZE * 2.75f;
            else if (enemy instanceof ZombieEnemy) enemy.maxVel = TILE_SIZE * 2.25f;
            else if (enemy instanceof GhostEnemy) enemy.maxVel = TILE_SIZE * 2f;
            enemy.setSlowed(false);
        }
        if (enemy.getBurnDuration() < 0) {
            enemy.setBurned(false);
            enemy.setBurnDuration(BURN_ENEMY_TIME);
        }
    }

    public static void loadEnemyAnimations(Enemy enemy, ObjectMap<String, Animation<AtlasRegion>> animations) {
        if (enemy instanceof SpiderEnemy) {
            animations.put(SPIDER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_RIGHT), LOOP));
            animations.put(SPIDER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_LEFT), LOOP));
            animations.put(SPIDER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_UP), LOOP));
            animations.put(SPIDER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ANIM_DOWN), LOOP));
            animations.put(SPIDER_ATTACK_RIGHT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_RIGHT), LOOP));
            animations.put(SPIDER_ATTACK_LEFT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_LEFT), LOOP));
            animations.put(SPIDER_ATTACK_UP, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_UP), LOOP));
            animations.put(SPIDER_ATTACK_DOWN, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_ATTACK_DOWN), LOOP));
            animations.put(SPIDER_DEAD_ANIM, new Animation<>(DEAD_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(SPIDER_DEAD_ANIM), NORMAL));
        } else if (enemy instanceof ZombieEnemy) {
            animations.put(ZOMBIE_ANIM_RIGHT, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_RIGHT), LOOP));
            animations.put(ZOMBIE_ANIM_LEFT, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_LEFT), LOOP));
            animations.put(ZOMBIE_ANIM_UP, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_UP), LOOP));
            animations.put(ZOMBIE_ANIM_DOWN, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ANIM_DOWN), LOOP));
            animations.put(ZOMBIE_ATTACK_RIGHT, new Animation<>(ATTACK_FRAME_DURATION * 1.5f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_RIGHT), LOOP));
            animations.put(ZOMBIE_ATTACK_LEFT, new Animation<>(ATTACK_FRAME_DURATION * 1.5f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_LEFT), LOOP));
            animations.put(ZOMBIE_ATTACK_UP, new Animation<>(ATTACK_FRAME_DURATION * 1.5f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_UP), LOOP));
            animations.put(ZOMBIE_ATTACK_DOWN, new Animation<>(ATTACK_FRAME_DURATION * 1.5f, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_ATTACK_DOWN), LOOP));
            animations.put(ZOMBIE_DEAD_ANIM, new Animation<>(DEAD_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(ZOMBIE_DEAD_ANIM), NORMAL));
        } else if (enemy instanceof GhostEnemy) {
            animations.put(GHOST_ANIM_RIGHT, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(GHOST_ANIM_RIGHT), LOOP));
            animations.put(GHOST_ANIM_LEFT, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(GHOST_ANIM_LEFT), LOOP));
            animations.put(GHOST_ANIM_UP, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(GHOST_ANIM_UP), LOOP));
            animations.put(GHOST_ANIM_DOWN, new Animation<>(FRAME_DURATION * 2f, Assets.instance.enemiesAtlas.findRegions(GHOST_ANIM_DOWN), LOOP));
            animations.put(GHOST_ATTACK_RIGHT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(GHOST_ATTACK_RIGHT), LOOP));
            animations.put(GHOST_ATTACK_LEFT, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(GHOST_ATTACK_LEFT), LOOP));
            animations.put(GHOST_ATTACK_UP, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(GHOST_ATTACK_UP), LOOP));
            animations.put(GHOST_ATTACK_DOWN, new Animation<>(ATTACK_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(GHOST_ATTACK_DOWN), LOOP));
            animations.put(GHOST_DEAD_ANIM, new Animation<>(DEAD_FRAME_DURATION, Assets.instance.enemiesAtlas.findRegions(GHOST_DEAD_ANIM), NORMAL));
        }

    }

    private static void enemyAnimation(Enemy enemy, String enemyAnim, SpriteBatch batch, Direction dir) {
        if (enemy instanceof SpiderEnemy) {
            if (enemy.isSlowed()) {
                enemy.animations.get(enemyAnim).setFrameDuration(FRAME_DURATION * 2);
            } else {
                enemy.animations.get(enemyAnim).setFrameDuration(FRAME_DURATION);
            }
        } else if (enemy instanceof ZombieEnemy) {
            if (enemy.isSlowed()) {
                enemy.animations.get(enemyAnim).setFrameDuration(FRAME_DURATION * 2.75f);
            } else {
                enemy.animations.get(enemyAnim).setFrameDuration(FRAME_DURATION * 1.75f);
            }

        } else if (enemy instanceof GhostEnemy) {
            if (enemy.isSlowed()) {
                enemy.animations.get(enemyAnim).setFrameDuration(FRAME_DURATION * 2.75f);
            } else {
                enemy.animations.get(enemyAnim).setFrameDuration(FRAME_DURATION * 2.5f);
            }

        }
        drawEntity(batch, enemy.animations.get(enemyAnim).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
        enemy.lastDir = dir;
    }

    private static void enemyStand(Enemy enemy, SpriteBatch batch) {
        if (enemy instanceof SpiderEnemy) {
            if (enemy.lastDir == RIGHT)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_RIGHT, 0), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == LEFT)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_LEFT, 0), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == UP)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_UP, 0), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == DOWN)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(SPIDER_ATTACK_DOWN, 0), enemy.getPos(), enemy.getSize());
        } else if (enemy instanceof ZombieEnemy) {
            if (enemy.lastDir == RIGHT)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_RIGHT), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == LEFT)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_LEFT), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == UP)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_UP), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == DOWN)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(ZOMBIE_STAND_DOWN), enemy.getPos(), enemy.getSize());
        } else if (enemy instanceof GhostEnemy) {
            if (enemy.lastDir == RIGHT)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(GHOST_STAND_RIGHT), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == LEFT)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(GHOST_STAND_LEFT), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == UP)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(GHOST_STAND_UP), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == DOWN)
                drawEntity(batch, Assets.instance.enemiesAtlas.findRegion(GHOST_STAND_DOWN), enemy.getPos(), enemy.getSize());
        }
    }

    private static void enemyAttackingAnimation(Enemy enemy, SpriteBatch batch) {
        if (enemy instanceof SpiderEnemy) {
            if (enemy.lastDir == RIGHT)
                drawEntity(batch, enemy.animations.get(SPIDER_ATTACK_RIGHT).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == LEFT)
                drawEntity(batch, enemy.animations.get(SPIDER_ATTACK_LEFT).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == UP)
                drawEntity(batch, enemy.animations.get(SPIDER_ATTACK_UP).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == DOWN)
                drawEntity(batch, enemy.animations.get(SPIDER_ATTACK_DOWN).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
        } else if (enemy instanceof ZombieEnemy) {
            if (enemy.lastDir == RIGHT)
                drawEntity(batch, enemy.animations.get(ZOMBIE_ATTACK_RIGHT).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == LEFT)
                drawEntity(batch, enemy.animations.get(ZOMBIE_ATTACK_LEFT).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == UP)
                drawEntity(batch, enemy.animations.get(ZOMBIE_ATTACK_UP).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == DOWN)
                drawEntity(batch, enemy.animations.get(ZOMBIE_ATTACK_DOWN).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
        } else if (enemy instanceof GhostEnemy) {
            if (enemy.lastDir == RIGHT)
                drawEntity(batch, enemy.animations.get(GHOST_ATTACK_RIGHT).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == LEFT)
                drawEntity(batch, enemy.animations.get(GHOST_ATTACK_LEFT).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == UP)
                drawEntity(batch, enemy.animations.get(GHOST_ATTACK_UP).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            else if (enemy.lastDir == DOWN)
                drawEntity(batch, enemy.animations.get(GHOST_ATTACK_DOWN).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
        }

    }

    public static void renderEnemyAnimations(Enemy enemy, SpriteBatch batch) {
        if (enemy instanceof SpiderEnemy) {
            if ((!enemy.isAttacking() && !enemy.isDead()) || (enemy.isFrozen() && !enemy.isDead())) {
                if (enemy.getVel().x < 0) {
                    enemyAnimation(enemy, SPIDER_ANIM_LEFT, batch, LEFT);
                } else if (enemy.getVel().x > 0) {
                    enemyAnimation(enemy, SPIDER_ANIM_RIGHT, batch, RIGHT);
                } else if (enemy.getVel().y > 0) {
                    enemyAnimation(enemy, SPIDER_ANIM_UP, batch, UP);
                } else if (enemy.getVel().y < 0) {
                    enemyAnimation(enemy, SPIDER_ANIM_DOWN, batch, DOWN);
                } else {
                    enemyStand(enemy, batch);
                }
            } else if (enemy.isAttacking() && !enemy.isDead()) {
                enemyAttackingAnimation(enemy, batch);

            } else {
                GameHelper.drawEntity(batch, enemy.animations.get(SPIDER_DEAD_ANIM).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            }
        } else if (enemy instanceof ZombieEnemy) {
            if ((!enemy.isAttacking() && !enemy.isDead()) || (enemy.isFrozen() && !enemy.isDead())) {
                if (enemy.getVel().x < 0) {
                    enemyAnimation(enemy, ZOMBIE_ANIM_LEFT, batch, LEFT);
                } else if (enemy.getVel().x > 0) {
                    enemyAnimation(enemy, ZOMBIE_ANIM_RIGHT, batch, RIGHT);
                } else if (enemy.getVel().y > 0) {
                    enemyAnimation(enemy, ZOMBIE_ANIM_UP, batch, UP);
                } else if (enemy.getVel().y < 0) {
                    enemyAnimation(enemy, ZOMBIE_ANIM_DOWN, batch, DOWN);
                } else {
                    enemyStand(enemy, batch);
                }
            } else if (enemy.isAttacking() && !enemy.isDead()) {
                enemyAttackingAnimation(enemy, batch);

            } else {
                GameHelper.drawEntity(batch, enemy.animations.get(ZOMBIE_DEAD_ANIM).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            }
        } else if (enemy instanceof GhostEnemy) {
            if ((!enemy.isAttacking() && !enemy.isDead()) || (enemy.isFrozen() && !enemy.isDead())) {
                if (enemy.getVel().x < 0) {
                    enemyAnimation(enemy, GHOST_ANIM_LEFT, batch, LEFT);
                } else if (enemy.getVel().x > 0) {
                    enemyAnimation(enemy, GHOST_ANIM_RIGHT, batch, RIGHT);
                } else if (enemy.getVel().y > 0) {
                    enemyAnimation(enemy, GHOST_ANIM_UP, batch, UP);
                } else if (enemy.getVel().y < 0) {
                    enemyAnimation(enemy, GHOST_ANIM_DOWN, batch, DOWN);
                } else {
                    enemyStand(enemy, batch);
                }
            } else if (enemy.isAttacking() && !enemy.isDead()) {
                enemyAttackingAnimation(enemy, batch);

            } else {
                GameHelper.drawEntity(batch, enemy.animations.get(GHOST_DEAD_ANIM).getKeyFrame(enemy.getStateTime()), enemy.getPos(), enemy.getSize());
            }
        }

    }

    public static void resizeGameWindow() {
        if (Gdx.input.isKeyPressed(Input.Keys.F11))
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.graphics.setWindowedMode(1280, 720);
    }

    private GameHelper() {
    }

    public static void updateEnemyStatus(Enemy e, SpriteBatch batch) {
        if (e.isBurned()) {
//            int[] colors = {
//                    0xFF0000FF, // Red
//                    0x00FF00FF, // Green
//                    0x0000FFFF, // Blue
//            };
//
//            int mixedColor = ColorUtils.mix(colors, 0, colors.length);
            batch.setPackedColor(Palette.BURNT_YELLOW);
        } else if (e.isSlowed()) {
            batch.setPackedColor(Palette.CALM_SKY);
        } else if (e.isFrozen()) {
            batch.setPackedColor(Palette.REFRESHING_MIST);
        }
    }
}
