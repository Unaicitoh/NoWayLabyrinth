package com.unaig.noway.util;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.unaig.noway.data.Assets;

public class GameHelper {
	
	public static Array<Rectangle> rects = new Array<>();
	public static Array<Polygon> polys = new Array<>();
	
	public static boolean checkCollisions(Rectangle r) {
		MapObjects collisions = Assets.instance.labMap.getLayers().get("Collisions").getObjects();
		for (int i = 0; i < collisions.getCount(); i++)
		{
		    MapObject mapObject = collisions.get(i);
		    if (mapObject instanceof RectangleMapObject)
		    {
		        Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
		        rects.add(rectangle);
		        if(r.overlaps(rectangle)) {
		        	return true;		        		
		        }
		        
		    }
		    else if (mapObject instanceof PolygonMapObject)
		    {
		        Polygon polygon = ((PolygonMapObject) mapObject).getPolygon();
		        polys.add(polygon);
		        
		        if(r.overlaps(polygon.getBoundingRectangle())) {
		        	return true;
	            }
		    }
		}
		return false;

	}
	
	private GameHelper() {}
	
}
