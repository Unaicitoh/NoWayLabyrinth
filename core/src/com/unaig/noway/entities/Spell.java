package com.unaig.noway.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.unaig.noway.data.Assets;
import com.unaig.noway.util.Constants;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.ElementType;
import com.unaig.noway.util.GameHelper;

public class Spell implements Poolable {

	public static final String TAG = Spell.class.getName();

	public boolean isAlive;
	private float timeAlive;
	private Vector2 pos;
	private Vector2 vel;
	private Vector2 size;
	private Rectangle spellBounds;
	private Direction playerLastDir;
	private ElementType type;
	private Animation<AtlasRegion> animation;
	private final float velMultipler = 2.5f;
	private final float offsetX = 2f;
	private final float offsetY = 2.5f;

	private static Pool<Spell> spellPool = Pools.get(Spell.class);
	
	public static Spell create(SpellPool pool,Player player) {
		Spell spell = spellPool.obtain();
		spell.init(player);
		pool.add(spell);
		return spell;
		
	}
	
	private void init(Player player) {
		isAlive=true;
		timeAlive=0;
		
		
		pos= new Vector2(player.getPos());
		vel=new Vector2(player.getVel().x*velMultipler,player.getVel().y*velMultipler);
		size = new Vector2(Constants.TILE_SIZE,Constants.TILE_SIZE);
		playerLastDir=player.lastDir;
		
		
		spellBounds= new Rectangle(pos.x+offsetX,pos.y+offsetY,size.x-offsetX*2,size.y-offsetY*2);		
		
		if((vel.x<0 && vel.y==0) || (vel.x==0 && vel.y==0 && player.lastDir==Direction.LEFT)){
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_LEFT), PlayMode.LOOP);
		}else if((vel.x>0 && vel.y==0) || (vel.x==0 && vel.y==0 && player.lastDir==Direction.RIGHT)) {
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_RIGHT), PlayMode.LOOP);
		}else if((vel.x==0 && vel.y>0) || (vel.x==0 && vel.y==0 && player.lastDir==Direction.UP)) {
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_UP), PlayMode.LOOP);
		}else if((vel.x==0 && vel.y<0) || (vel.x==0 && vel.y==0 && player.lastDir==Direction.DOWN)) {
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_DOWN), PlayMode.LOOP);
		}else if(vel.x<0 && vel.y>0) {
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_LEFT_UP), PlayMode.LOOP);
		}else if(vel.x<0 && vel.y<0) {
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_LEFT_DOWN), PlayMode.LOOP);
		}else if(vel.x>0 && vel.y>0) {
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_RIGHT_UP), PlayMode.LOOP);
		}else if(vel.x>0 && vel.y<0) {
			animation = new Animation<>(0.05f, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_RIGHT_DOWN), PlayMode.LOOP);
		}
	}
	
	public void update(float delta) {
		timeAlive+=delta;
		if(vel.x==0 && vel.y==0) {
			switch (playerLastDir) {
			case LEFT:
				vel.x=-Player.MAX_VEL*velMultipler;
				break;
			case DOWN:
				vel.y=-Player.MAX_VEL*velMultipler;
				break;
			case RIGHT:
				vel.x=Player.MAX_VEL*velMultipler;
				break;
			case UP:
				vel.y=Player.MAX_VEL*velMultipler;
				break;
				
			}
		}else {
			Gdx.app.log(TAG, ""+vel);
			pos.x+=vel.x*delta;
			pos.y+=vel.y*delta;
			spellBounds.setPosition(pos.x+offsetX,pos.y+offsetY);
		}
		
		if(GameHelper.checkCollisions(spellBounds)) 
			isAlive=false;
		

	}
	
	public void draw(SpriteBatch batch, float delta) {
		update(delta);
		batch.draw(animation.getKeyFrame(timeAlive), pos.x, pos.y, size.x,size.y);
	}

	@Override
	public void reset() {
		isAlive=false;
		isAlive=true;
		timeAlive=0;
		pos=new Vector2();
		vel=new Vector2();
		spellBounds= new Rectangle();
	}
	public void release() {
		spellPool.free(this);
	}

	public Rectangle getSpellBounds() {
		return spellBounds;
	}

	public void setSpellBounds(Rectangle spellBounds) {
		this.spellBounds = spellBounds;
	}


}
