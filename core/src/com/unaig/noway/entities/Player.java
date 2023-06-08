package com.unaig.noway.entities;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.spells.FireSpell;
import com.unaig.noway.entities.spells.IceSpell;
import com.unaig.noway.util.AttackType;
import com.unaig.noway.util.Constants;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.ElementType;
import com.unaig.noway.util.GameHelper;

public class Player extends Entity implements InputProcessor{

	public static final String TAG = Player.class.getName();

	public SpellPool spellPool;
	private ElementType elementType;
	private static final float OFFSET_X = 2f;
	private static final float FRAME_DURATION = 0.1f;

	public Player(SpellPool spellPool) {
		init(spellPool);
	}
	
	protected void init(SpellPool spellPool) {
		int rnd = MathUtils.random(1);
		elementType=ElementType.FIRE;
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
		maxVel = Constants.TILE_SIZE*3;
		bounds = new Rectangle(pos.x+OFFSET_X, pos.y, size.x-OFFSET_X*2, size.y);
		lastDir=Direction.RIGHT;
		animations=new ObjectMap<>();
		animations.put(Constants.PLAYER_ANIM_RIGHT, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_RIGHT), PlayMode.LOOP));
		animations.put(Constants.PLAYER_ANIM_LEFT, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_LEFT), PlayMode.LOOP));
		animations.put(Constants.PLAYER_ANIM_UP, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_UP), PlayMode.LOOP));
		animations.put(Constants.PLAYER_ANIM_DOWN, new Animation<>(FRAME_DURATION, Assets.instance.playerAtlas.findRegions(Constants.PLAYER_ANIM_DOWN), PlayMode.LOOP));
		this.spellPool= spellPool;
		stateTime=0f;
		
	}

	public void render(SpriteBatch batch, float delta) {
		stateTime+=delta;
		update(delta);
		
		if(vel.x<0) {
			GameHelper.drawEntity(batch,animations.get(Constants.PLAYER_ANIM_LEFT).getKeyFrame(stateTime),pos,size);
			lastDir= Direction.LEFT;
		}else if(vel.x>0) {
			GameHelper.drawEntity(batch,animations.get(Constants.PLAYER_ANIM_RIGHT).getKeyFrame(stateTime),pos,size);
			lastDir= Direction.RIGHT;

		}else if(vel.y>0) {
			GameHelper.drawEntity(batch,animations.get(Constants.PLAYER_ANIM_UP).getKeyFrame(stateTime),pos,size);
			lastDir= Direction.UP;

		}else if(vel.y<0) {
			GameHelper.drawEntity(batch,animations.get(Constants.PLAYER_ANIM_DOWN).getKeyFrame(stateTime),pos,size);
			lastDir= Direction.DOWN;

		}else {
			if(lastDir==Direction.RIGHT) GameHelper.drawEntity(batch,Assets.instance.playerAtlas.findRegion(Constants.PLAYER_ANIM_RIGHT, 0),pos,size);
			else if (lastDir==Direction.LEFT) GameHelper.drawEntity(batch,Assets.instance.playerAtlas.findRegion(Constants.PLAYER_ANIM_LEFT, 0),pos,size);
			else if (lastDir==Direction.UP) GameHelper.drawEntity(batch,Assets.instance.playerAtlas.findRegion(Constants.PLAYER_ANIM_UP, 0),pos,size);
			else if (lastDir==Direction.DOWN) GameHelper.drawEntity(batch,Assets.instance.playerAtlas.findRegion(Constants.PLAYER_ANIM_DOWN, 0),pos,size);
		}
		
	}

	public void update(float delta) {
			vel.x = MathUtils.clamp(vel.x, -maxVel, maxVel);
			vel.y = MathUtils.clamp(vel.y, -maxVel, maxVel);
		    Vector2 lastValidPos = new Vector2(pos);
		    
		    // Move horizontally
		    pos.x += vel.x * delta;
		    bounds.setPosition(pos.x+OFFSET_X, pos.y);
		    if (GameHelper.checkCollisions(bounds)) {
		        pos.x = lastValidPos.x;
		    } else {
		        lastValidPos.x = pos.x; 
		    }
		    bounds.setPosition(pos.x+OFFSET_X, pos.y);

		    // Move vertically
		    pos.y += vel.y * delta;
		    bounds.setPosition(pos.x+OFFSET_X, pos.y);
		    if (GameHelper.checkCollisions(bounds)) {
		        pos.y = lastValidPos.y;
		    } else {
		        lastValidPos.y = pos.y;
		    }
		    bounds.setPosition(pos.x+OFFSET_X, pos.y);
		    pos.set(lastValidPos); 
			
		}
		
	

	@Override
	public boolean keyDown(int keycode) {
		switch(keycode) {
		case Keys.D:
			vel.x=maxVel;
			break;
		case Keys.A:
			vel.x=-maxVel;
			break;
		case Keys.W:
			vel.y=maxVel;
			break;
		case Keys.S:
			vel.y=-maxVel;
			break;
		case Keys.SPACE:
			changeElement();
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
				if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-maxVel;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=maxVel;
				else if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-maxVel;
			}
			stateTime=0f;
			break;
		case Keys.A:
			if(vel.x<0) {
				vel.x=0;
				if(Gdx.input.isKeyPressed(Keys.D)) vel.x=maxVel;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=maxVel;
				else if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-maxVel;
			}
			stateTime=0f;
			break;
		case Keys.W:
			if(vel.y>0) {
				vel.y=0;
				if(Gdx.input.isKeyPressed(Keys.S)) vel.y=-maxVel;
				if(Gdx.input.isKeyPressed(Keys.D)) vel.x=maxVel;
				else if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-maxVel;
			}
			stateTime=0f;
			break;
		case Keys.S:
			if(vel.y<0) {
				vel.y=0;
				if(Gdx.input.isKeyPressed(Keys.W)) vel.y=maxVel;
				if(Gdx.input.isKeyPressed(Keys.D)) vel.x=maxVel;
				else if(Gdx.input.isKeyPressed(Keys.A)) vel.x=-maxVel;
			}
			stateTime=0f;
			break;
		}
		return true;
	}

	private void changeElement() {
		if(elementType==ElementType.FIRE) {
			elementType=ElementType.ICE; 		
		}else if(elementType==ElementType.ICE) {
			elementType=ElementType.FIRE;
		}
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		switch (button) {
		case Buttons.LEFT:
			if(elementType==ElementType.FIRE)
				FireSpell.create(spellPool, this, AttackType.BASIC);
			else if(elementType==ElementType.ICE)
				IceSpell.create(spellPool, this, AttackType.BASIC);
			break;
		case Buttons.RIGHT:
			if(elementType==ElementType.FIRE)
				FireSpell.create(spellPool, this, AttackType.STRONG);
			else if(elementType==ElementType.ICE)
				IceSpell.create(spellPool, this, AttackType.STRONG);
			break;
		}
		return true;
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
