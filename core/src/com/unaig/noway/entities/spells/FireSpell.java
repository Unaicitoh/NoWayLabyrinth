package com.unaig.noway.entities.spells;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.PoolEngine;
import com.unaig.noway.util.AttackType;
import com.unaig.noway.util.Constants;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.Constants.*;

public class FireSpell extends Spell{


	private static Pool<FireSpell> spellPool = Pools.get(FireSpell.class);

	public static void create(PoolEngine pool, Player player, AttackType type) {
		FireSpell spell = spellPool.obtain();
		spell.init(player, type);
		pool.add(spell);
	}
	@Override
	protected void init(Player player, AttackType attackType) {
		super.init(player, attackType);
		setFireAnimations(attackType);
	}

	private void setFireAnimations(AttackType attackType) {
		if(attackType==AttackType.BASIC) {
			basicAttackAnimations();
		}else if(attackType==AttackType.STRONG) {
			strongAttackAnimations();
		}		
	}
	private void basicAttackAnimations() {
		if((vel.x<0 && vel.y==0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.LEFT)){
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_LEFT);
		}else if((vel.x>0 && vel.y==0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.RIGHT)) {
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_RIGHT);
		}else if((vel.x==0 && vel.y>0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.UP)) {
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_UP);
		}else if((vel.x==0 && vel.y<0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.DOWN)) {
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_DOWN);
		}else if(vel.x<0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_LEFT_UP);
		}else if(vel.x<0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_LEFT_DOWN);
		}else if(vel.x>0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_RIGHT_UP);
		}else if(vel.x>0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, FIRE_SPELL_RIGHT_DOWN);

		}		
	}
	
	private void strongAttackAnimations() {
		if((vel.x<0 && vel.y==0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.LEFT)){
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_LEFT);
		}else if((vel.x>0 && vel.y==0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.RIGHT)) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_RIGHT);
		}else if((vel.x==0 && vel.y>0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.UP)) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_UP);
		}else if((vel.x==0 && vel.y<0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.DOWN)) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_DOWN);
		}else if(vel.x<0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_LEFT_UP);
		}else if(vel.x<0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_LEFT_DOWN);
		}else if(vel.x>0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_RIGHT_UP);
		}else if(vel.x>0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, FIRE2_SPELL_RIGHT_DOWN);
		}
		
	}
	@Override
	public void release() {
		spellPool.free(this);
	}
	
}
