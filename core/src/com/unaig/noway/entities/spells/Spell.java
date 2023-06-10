package com.unaig.noway.entities.spells;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.unaig.noway.entities.Player;
import com.unaig.noway.util.AttackType;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.Constants.TILE_SIZE;

public abstract class Spell implements Poolable {

	public static final String TAG = Spell.class.getName();

	protected static final float FRAME_DURATION = .06f;
	protected static final float FRAME_DURATION_STRONG = .075f;
	
	public boolean isAlive;
	private float timeAlive;
	
	private Vector2 pos;
	protected Vector2 vel;
	private Vector2 size;
	private Rectangle spellBounds;
	protected Direction playerLastDir;
	protected Animation<AtlasRegion> animation;
	protected float velMultiplier;
	private static final float OFFSET_X = 2f;
	private static final float OFFSET_Y = 2.5f;
	private static final float LIFE_DURATION = 3f;
	protected AttackType attackType;
	
	protected void init(Player player, AttackType attackType) {
		isAlive=true;
		timeAlive=0;
		if(attackType==AttackType.STRONG) velMultiplier =2.0f;
		else velMultiplier =2.5f;
		pos= new Vector2(player.getPos());
		vel=new Vector2(player.getVel().x* velMultiplier,player.getVel().y* velMultiplier);
		size = new Vector2(TILE_SIZE, TILE_SIZE);
		playerLastDir=player.lastDir;
		spellBounds= new Rectangle(pos.x+OFFSET_X,pos.y+OFFSET_Y,size.x-OFFSET_X*2,size.y-OFFSET_Y*2);	
		this.attackType=attackType;
	}
	
	public void update(float delta) {
		timeAlive+=delta;
		vel.x = MathUtils.clamp(vel.x, -Player.maxVel* velMultiplier, Player.maxVel* velMultiplier);
		vel.y = MathUtils.clamp(vel.y, -Player.maxVel* velMultiplier, Player.maxVel* velMultiplier);
		if(vel.x==0 && vel.y==0) {
			switch (playerLastDir) {
			case LEFT:
				vel.x=-Player.maxVel* velMultiplier;
				break;
			case DOWN:
				vel.y=-Player.maxVel* velMultiplier;
				break;
			case RIGHT:
				vel.x=Player.maxVel* velMultiplier;
				break;
			case UP:
				vel.y=Player.maxVel* velMultiplier;
				break;
				
			}
		}else {
			pos.x+=vel.x*delta;
			pos.y+=vel.y*delta;
			spellBounds.setPosition(pos.x+OFFSET_X,pos.y+OFFSET_Y);
		}
		
		if(GameHelper.checkCollisions(spellBounds) || timeAlive>LIFE_DURATION) {
			isAlive=false;
		}
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
		velMultiplier = 2.5f;
	}
	
	public abstract void release();

	public Rectangle getSpellBounds() {
		return spellBounds;
	}

	public void setSpellBounds(Rectangle spellBounds) {
		this.spellBounds = spellBounds;
	}


}
