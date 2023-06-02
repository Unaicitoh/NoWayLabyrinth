package com.unaig.noway.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.unaig.noway.data.Assets;
import com.unaig.noway.util.Constants;

public class Player implements InputProcessor{
	public static final String TAG = Player.class.getName();

	private Vector2 pos = new Vector2(Constants.TILE_SIZE*2,Constants.TILE_SIZE*2);
	private Vector2 vel = new Vector2(0,0);
	private Vector2 size = new Vector2(Constants.TILE_SIZE,Constants.TILE_SIZE);
	private Animation<TextureRegion> runRight;
	private Animation<TextureRegion> runLeft;
	private Animation<TextureRegion> runUp;
	private Animation<TextureRegion> runDown;
	private Animation<TextureRegion> stand;

	private float stateTime;
	private static final float MAX_VEL = Constants.TILE_SIZE*3;
	
	public Player() {
		runRight =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runRight"), PlayMode.LOOP);
		runLeft =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runLeft"), PlayMode.LOOP);
		runUp =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runUp"), PlayMode.LOOP);
		runDown =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runDown"), PlayMode.LOOP);
		stand =
			    new Animation<TextureRegion>(1.5f, Assets.instance.playerAtlas.findRegions("stand"), PlayMode.LOOP);
		stateTime=0f;
	}
	
	public void render(SpriteBatch batch) {
		stateTime+=Gdx.graphics.getDeltaTime();
		update(Gdx.graphics.getDeltaTime());
		if(vel.x<0) {
			batch.draw(runLeft.getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);
		}else if(vel.x>0) {
			batch.draw(runRight.getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);

		}else if(vel.y>0) {
			batch.draw(runUp.getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);

		}else if(vel.y<0) {
			batch.draw(runDown.getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);

		}else{
			batch.draw(stand.getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);
		}
		
	}

	public void update(float delta) {
			vel.x = MathUtils.clamp(vel.x, -MAX_VEL, MAX_VEL);
			vel.y = MathUtils.clamp(vel.y, -MAX_VEL, MAX_VEL);
			checkCollisions();
			pos.x+=vel.x*delta;
			pos.y+=vel.y*delta;
			
		}
		

	private void checkCollisions() {
		// Get tile layer
		TiledMapTileLayer layer = (TiledMapTileLayer) Assets.instance.labMap.getLayers().get(0);
		Rectangle playerBounds = new Rectangle(pos.x, pos.y, Constants.TILE_SIZE, Constants.TILE_SIZE);
		for (int x = 0; x < layer.getWidth(); x++)
		{
		    for (int y = 0; y < layer.getHeight(); y++)
		    {
		        // Get cell and check if theres a tile in that cell
		        Cell cell = layer.getCell(x, y);
		        if (cell == null)
		            continue;

		        // Get the tiles collision object
		        MapObjects cellObjects = cell.getTile().getObjects();
		   
		        // Here I only get the first one, maybe you have multiple, up to you how to parse them
		        if (cellObjects.getCount() == 0)
		            continue;

		        MapObject mapObject = cellObjects.get(0);

		        // Converts it to its proper object type
		        if (mapObject instanceof RectangleMapObject)
		        {
		            
		            Rectangle rectangle =  ((RectangleMapObject) mapObject).getRectangle();
		            
		            if(Intersector.overlaps(playerBounds, rectangle)) {
		            	Gdx.app.log(TAG, "Hitting wall");
//		            	vel.set(0, 0);
		            }
		            
		        } 
		        else if (mapObject instanceof PolygonMapObject)
		        {
		            Polygon polygon = ((PolygonMapObject) mapObject).getPolygon();
		            if(Intersector.overlaps(playerBounds, polygon.getBoundingRectangle())) {
		            	Gdx.app.log(TAG, "Hitting wall");
//		            	vel.set(0, 0);
		            }
		        }	
		    }
		}
	}

	public Vector2 getPos() {
		return pos;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;
	}

	public Vector2 getVel() {
		return vel;
	}

	public void setVel(Vector2 vel) {
		this.vel = vel;
	}

	@Override
	public boolean keyDown(int keycode) {

		switch(keycode) {
		case Keys.D:
			vel.x=MAX_VEL;
			break;
		case Keys.A:
			vel.x=-MAX_VEL;
			break;
		case Keys.W:
			vel.y=MAX_VEL;
			break;
		case Keys.S:
			vel.y=-MAX_VEL;
			break;
		
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		switch(keycode) {
		case Keys.D:
			if(vel.x>0)
			vel.x=0;
			stateTime=0f;
			break;
		case Keys.A:
			if(vel.x<0)
			vel.x=0;
			stateTime=0f;
			break;
		case Keys.W:
			if(vel.y>0)
			vel.y=0;
			stateTime=0f;
			break;
		case Keys.S:
			if(vel.y<0)
			vel.y=0;
			stateTime=0f;
			break;
		}
		

		return true;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
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
