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

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP;
import static com.unaig.noway.util.Constants.DAMAGE_ANIMATION_TIME;

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
        if (e.getIsDamaged() && e.timeDamageTaken>=0) {
            e.timeDamageTaken-= delta;
            batch.setColor(1,0,0,e.timeDamageTaken*5);
        } else {
            e.setIsDamaged(false);
            e.timeDamageTaken= DAMAGE_ANIMATION_TIME;
            batch.setColor(1,1,1,1);
        }
    }

    private GameHelper() {
    }

}
