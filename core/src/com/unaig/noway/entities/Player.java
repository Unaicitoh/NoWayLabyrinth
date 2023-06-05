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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.unaig.noway.data.Assets;
import com.unaig.noway.util.Constants;

public class Player implements InputProcessor{
	public static final String TAG = Player.class.getName();

	private Vector2 pos;
	private Vector2 lastPos;
	private Vector2 vel;
	private Vector2 size;
	private Animation<TextureRegion> runRight;
	private Animation<TextureRegion> runLeft;
	private Animation<TextureRegion> runUp;
	private Animation<TextureRegion> runDown;
	private Animation<TextureRegion> stand;
	private Rectangle playerBounds;
	public Array<Rectangle> rects = new Array<>();
	public Array<Polygon> polys = new Array<>();

	private float stateTime;

	public float MAX_VEL = Constants.TILE_SIZE*4;
	
	public Player() {
		init();
	}
	
	private void init() {
		pos = new Vector2(Constants.TILE_SIZE*2,Constants.TILE_SIZE*2);
		lastPos = new Vector2();
		vel = new Vector2(0,0);
		size = new Vector2(Constants.TILE_SIZE-4,Constants.TILE_SIZE);
		playerBounds = new Rectangle(pos.x, pos.y, size.x-4, size.y);
		
		runRight =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runRight"), PlayMode.LOOP);
		runLeft =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runLeft"), PlayMode.LOOP);
		runUp =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runUp"), PlayMode.LOOP);
		runDown =
			    new Animation<TextureRegion>(0.1f, Assets.instance.playerAtlas.findRegions("runDown"), PlayMode.LOOP);
		stand =
			    new Animation<TextureRegion>(1.25f, Assets.instance.playerAtlas.findRegions("stand"), PlayMode.LOOP);
		stateTime=0f;
	}

	public void render(SpriteBatch batch, float delta) {
		stateTime+=delta;
		update(delta);
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
			lastPos.x=pos.x;
			lastPos.y=pos.y;			
			pos.x+=vel.x*delta;
			pos.y+=vel.y*delta;
			playerBounds.setPosition(pos.x+2, pos.y);
			checkCollisions(delta);
			
		}
		

	private void checkCollisions(float delta) {

		MapObjects collisions = Assets.instance.labMap.getLayers().get("Collisions").getObjects();
		for (int i = 0; i < collisions.getCount(); i++)
		{
		    MapObject mapObject = collisions.get(i);
		    if (mapObject instanceof RectangleMapObject)
		    {
		        Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
		        rects.add(rectangle);
		        if(playerBounds.overlaps(rectangle)) {
		        	pos.x=lastPos.x;
		        	pos.y=lastPos.y;
		        }
		        
		    }
		    else if (mapObject instanceof PolygonMapObject)
		    {
		        Polygon polygon = ((PolygonMapObject) mapObject).getPolygon();
		        polys.add(polygon);
		        
		        if(playerBounds.overlaps(polygon.getBoundingRectangle())) {
		        	pos.x=lastPos.x;
		        	pos.y=lastPos.y;
	            }
		    }
		}
		playerBounds.setPosition(pos.x+2, pos.y);

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

	public Rectangle getPlayerBounds() {
		return playerBounds;
	}

	public void setPlayerBounds(Rectangle playerBounds) {
		this.playerBounds = playerBounds;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
		case Keys.D:
			vel.y=0;
			vel.x=MAX_VEL;
			break;
		case Keys.A:
			vel.y=0;
			vel.x=-MAX_VEL;
			break;
		case Keys.W:
			vel.x=0;
			vel.y=MAX_VEL;
			break;
		case Keys.S:
			vel.x=0;
			vel.y=-MAX_VEL;
			break;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch(keycode) {
		case Keys.D:
			if(vel.x>0) {
				vel.x=0;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-MAX_VEL;
			}
			stateTime=0f;
			break;
		case Keys.A:
			if(vel.x<0) {
				vel.x=0;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.D)) vel.x=MAX_VEL;
			}
			stateTime=0f;
			break;
		case Keys.W:
			if(vel.y>0) {
				vel.y=0;
				if(Gdx.input.isKeyPressed(Keys.D)) vel.x=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-MAX_VEL;
			}
			stateTime=0f;
			break;
		case Keys.S:
			if(vel.y<0) {
				vel.y=0;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.D)) vel.x=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-MAX_VEL;
			}
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
