package com.unaig.noway.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.unaig.noway.data.Assets;
import com.unaig.noway.util.Constants;

public class Player implements InputProcessor{
	public static final String TAG = Player.class.getName();

	private Vector2 pos = new Vector2(Constants.TILE_SIZE*2,Constants.TILE_SIZE*2);
	private Vector2 vel = new Vector2(0,0);
	private Sprite sprite = new Sprite();
	private Animation<TextureRegion> runRight;
	private Animation<TextureRegion> runLeft;
	private Animation<TextureRegion> runUp;
	private Animation<TextureRegion> runDown;

	private float stateTime;
	private static final float MAX_VEL = Constants.TILE_SIZE*3;
	
	public Player(Sprite sprite) {
		this.sprite=sprite;
		runRight =
			    new Animation<TextureRegion>(0.08f, Assets.instance.playerAtlas.findRegions("runRight"), PlayMode.LOOP);
		runLeft =
			    new Animation<TextureRegion>(0.08f, Assets.instance.playerAtlas.findRegions("runLeft"), PlayMode.LOOP);
		runUp =
			    new Animation<TextureRegion>(0.08f, Assets.instance.playerAtlas.findRegions("runUp"), PlayMode.LOOP);
		runDown =
			    new Animation<TextureRegion>(0.08f, Assets.instance.playerAtlas.findRegions("runDown"), PlayMode.LOOP);
		stateTime=0f;
		this.sprite.setSize(Constants.TILE_SIZE,Constants.TILE_SIZE);
	}
	
	public void render(SpriteBatch batch) {
		stateTime+=Gdx.graphics.getDeltaTime();
		update(Gdx.graphics.getDeltaTime());
		if(vel.x<0) {
			batch.draw(runLeft.getKeyFrame(stateTime),pos.x,pos.y,Constants.TILE_SIZE,Constants.TILE_SIZE);
		}else if(vel.x>0) {
			batch.draw(runRight.getKeyFrame(stateTime),pos.x,pos.y,Constants.TILE_SIZE,Constants.TILE_SIZE);

		}else if(vel.y>0) {
			batch.draw(runUp.getKeyFrame(stateTime),pos.x,pos.y,Constants.TILE_SIZE,Constants.TILE_SIZE);

		}else if(vel.y<0) {
			batch.draw(runDown.getKeyFrame(stateTime),pos.x,pos.y,Constants.TILE_SIZE,Constants.TILE_SIZE);

		}else if(vel.x==0) {
			sprite.draw(batch);
		}
		
	}

	public void update(float delta) {
			vel.x = MathUtils.clamp(vel.x, -MAX_VEL, MAX_VEL);
			vel.y = MathUtils.clamp(vel.y, -MAX_VEL, MAX_VEL);
			pos.x+=vel.x*delta;
			pos.y+=vel.y*delta;
			sprite.setPosition(pos.x, pos.y);
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
			vel.x=0;
			break;
		case Keys.A:
			vel.x=0;
			break;
		case Keys.W:
			vel.y=0;
			break;
		case Keys.S:
			vel.y=0;
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
