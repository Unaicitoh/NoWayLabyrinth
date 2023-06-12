package com.unaig.noway.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.unaig.noway.data.Assets;

public class GameHelper {

	public static final String TAG = GameHelper.class.getName();

	public static boolean checkCollisions(Rectangle r) {
		MapObjects collisions = Assets.instance.labMap.getLayers().get("Collisions").getObjects();
		for (int i = 0; i < collisions.getCount(); i++)
		{
		    MapObject mapObject = collisions.get(i);
		    if (mapObject instanceof RectangleMapObject)
		    {
		        Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
		        if(r.overlaps(rectangle)) {
		        	return true;		        		
		        }
		        
		    }
		    else if (mapObject instanceof PolygonMapObject)
		    {
		        Polygon polygon = ((PolygonMapObject) mapObject).getPolygon();
		        if(r.overlaps(polygon.getBoundingRectangle())) {
		        	return true;
	            }
		    }
		}
		return false;
	}
	
	public static Animation<AtlasRegion> setAnimation(float duration, String animationName) {
		return new Animation<>(duration, Assets.instance.playerAtlas.findRegions(animationName), PlayMode.LOOP);
	}
	
	public static void drawEntity(SpriteBatch batch, AtlasRegion atlasRegion, Vector2 pos, Vector2 size) {
		batch.draw(atlasRegion,pos.x,pos.y,size.x,size.y);
	}

	private GameHelper() {}
	
}
