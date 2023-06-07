package com.unaig.noway.entities.spells;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.unaig.noway.entities.Player;
import com.unaig.noway.util.AttackType;
import com.unaig.noway.util.Constants;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

public abstract class Spell implements Poolable {

	public static final String TAG = Spell.class.getName();

	public boolean isAlive;
	private float timeAlive;
	private Vector2 pos;
	protected Vector2 vel;
	private Vector2 size;
	private Rectangle spellBounds;
	protected Direction playerLastDir;
	protected Animation<AtlasRegion> animation;
	protected float velMultipler;
	private final float offsetX = 2f;
	private final float offsetY = 2.5f;
	protected AttackType attackType;
	
	protected void init(Player player, AttackType attackType) {
		isAlive=true;
		timeAlive=0;
		if(attackType==AttackType.STRONG) velMultipler=2.0f;
		else velMultipler=2.5f;
		pos= new Vector2(player.getPos());
		vel=new Vector2(player.getVel().x*velMultipler,player.getVel().y*velMultipler);
		size = new Vector2(Constants.TILE_SIZE,Constants.TILE_SIZE);
		playerLastDir=player.lastDir;
		spellBounds= new Rectangle(pos.x+offsetX,pos.y+offsetY,size.x-offsetX*2,size.y-offsetY*2);	
		this.attackType=attackType;
	}
	
	public void update(float delta) {
		timeAlive+=delta;
		vel.x = MathUtils.clamp(vel.x, -Player.MAX_VEL*velMultipler, Player.MAX_VEL*velMultipler);
		vel.y = MathUtils.clamp(vel.y, -Player.MAX_VEL*velMultipler, Player.MAX_VEL*velMultipler);
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
			Gdx.app.log(TAG, "update2"+vel);
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
		playerLastDir=null;
		velMultipler = 2.5f;
	}
	
	public abstract void release();

	public Rectangle getSpellBounds() {
		return spellBounds;
	}

	public void setSpellBounds(Rectangle spellBounds) {
		this.spellBounds = spellBounds;
	}


}
