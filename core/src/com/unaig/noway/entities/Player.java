package com.unaig.noway.entities;


import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.unaig.noway.data.Assets;
import com.unaig.noway.util.Constants;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.ElementType;
import com.unaig.noway.util.GameHelper;

public class Player implements InputProcessor{
	public static final String TAG = Player.class.getName();

	private Vector2 pos;
	private Vector2 vel;
	private Vector2 size;
	private ObjectMap<String, Animation<AtlasRegion>> animations;
	private Rectangle playerBounds;
	public Direction lastDir;
	private ElementType type;
	
	public SpellPool spellPool;
	
	private float stateTime;
	private final float offsetX = 2f;
	public static float MAX_VEL = Constants.TILE_SIZE*3;
	
	public Player(SpellPool spellPool) {
		init(spellPool);
	}
	
	private void init(SpellPool spellPool) {
		int rnd = MathUtils.random(1);
		Gdx.app.log(TAG, ""+rnd);
		size = new Vector2(Constants.TILE_SIZE,Constants.TILE_SIZE);
		MapObjects collisions = Assets.instance.labMap.getLayers().get("Spawns").getObjects();
		for (int i = 0; i < collisions.getCount(); i++)
		{
		    MapObject mapObject = collisions.get(i);
		    
		    if (rnd==0)
		    {
		    	if(mapObject.getName().equals("PlayerSpawn")) {
		    		Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
		    		pos = new Vector2(rectangle.x-size.x/2,rectangle.y);		    		
		    	}
		    }else {
		    		Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
		    		pos = new Vector2(rectangle.x-size.x/2,rectangle.y);		    		
		    }
		}
		
		vel = new Vector2(0,0);
		playerBounds = new Rectangle(pos.x+offsetX, pos.y, size.x-offsetX*2, size.y);
		lastDir=Direction.RIGHT;
		animations=new ObjectMap<>();
		animations.put(Constants.PLAYER_ANIM_RIGHT, new Animation<>(0.1f, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_RIGHT), PlayMode.LOOP));
		animations.put(Constants.PLAYER_ANIM_LEFT, new Animation<>(0.1f, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_LEFT), PlayMode.LOOP));
		animations.put(Constants.PLAYER_ANIM_UP, new Animation<>(0.1f, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_UP), PlayMode.LOOP));
		animations.put(Constants.PLAYER_ANIM_DOWN, new Animation<>(0.1f, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_DOWN), PlayMode.LOOP));
		this.spellPool= spellPool;
		stateTime=0f;
		
	}

	public void render(SpriteBatch batch, float delta) {
		stateTime+=delta;
		update(delta);
		
		if(vel.x<0) {
			batch.draw(animations.get(Constants.PLAYER_ANIM_LEFT).getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);
			lastDir= Direction.LEFT;
		}else if(vel.x>0) {
			batch.draw(animations.get(Constants.PLAYER_ANIM_RIGHT).getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);
			lastDir= Direction.RIGHT;

		}else if(vel.y>0) {
			batch.draw(animations.get(Constants.PLAYER_ANIM_UP).getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);
			lastDir= Direction.UP;

		}else if(vel.y<0) {
			batch.draw(animations.get(Constants.PLAYER_ANIM_DOWN).getKeyFrame(stateTime),pos.x,pos.y,size.x,size.y);
			lastDir= Direction.DOWN;

		}else {
			if(lastDir==Direction.RIGHT) batch.draw(Assets.instance.playerAtlas.findRegion("runRight", 0), pos.x,pos.y,size.x,size.y);
			else if (lastDir==Direction.LEFT) batch.draw(Assets.instance.playerAtlas.findRegion("runLeft", 0), pos.x,pos.y,size.x,size.y);
			else if (lastDir==Direction.UP) batch.draw(Assets.instance.playerAtlas.findRegion("runUp", 0), pos.x,pos.y,size.x,size.y);
			else if (lastDir==Direction.DOWN) batch.draw(Assets.instance.playerAtlas.findRegion("runDown", 0), pos.x,pos.y,size.x,size.y);
		}
		
	}

	public void update(float delta) {
			vel.x = MathUtils.clamp(vel.x, -MAX_VEL, MAX_VEL);
			vel.y = MathUtils.clamp(vel.y, -MAX_VEL, MAX_VEL);
		    Vector2 lastValidPos = new Vector2(pos);
		    
		    // Move horizontally
		    pos.x += vel.x * delta;
		    playerBounds.setPosition(pos.x+offsetX, pos.y);
		    if (GameHelper.checkCollisions(playerBounds)) {
		        pos.x = lastValidPos.x;
		    } else {
		        lastValidPos.x = pos.x; 
		    }
		    playerBounds.setPosition(pos.x+offsetX, pos.y);

		    // Move vertically
		    pos.y += vel.y * delta;
		    playerBounds.setPosition(pos.x+offsetX, pos.y);
		    if (GameHelper.checkCollisions(playerBounds)) {
		        pos.y = lastValidPos.y;
		    } else {
		        lastValidPos.y = pos.y;
		    }
		    playerBounds.setPosition(pos.x+offsetX, pos.y);
		    pos.set(lastValidPos); 
			
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

	public Vector2 getSize() {
		return size;
	}

	public void setSize(Vector2 size) {
		this.size = size;
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
		case Keys.SPACE:
			Spell.create(spellPool, this);
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
				if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-MAX_VEL;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-MAX_VEL;
			}
			stateTime=0f;
			break;
		case Keys.A:
			if(vel.x<0) {
				vel.x=0;
				if(Gdx.input.isKeyPressed(Keys.D)) vel.x=MAX_VEL;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-MAX_VEL;
			}
			stateTime=0f;
			break;
		case Keys.W:
			if(vel.y>0) {
				vel.y=0;
				if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-MAX_VEL;
				if(Gdx.input.isKeyPressed(Keys.D)) vel.x=MAX_VEL;
				else if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-MAX_VEL;
			}
			stateTime=0f;
			break;
		case Keys.S:
			if(vel.y<0) {
				vel.y=0;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=MAX_VEL;
				if(Gdx.input.isKeyPressed(Keys.D)) vel.x=MAX_VEL;
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
