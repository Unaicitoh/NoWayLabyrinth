package com.unaig.noway.entities.spells;

import com.badlogic.gdx.utils.Pool;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.Player;
import com.unaig.noway.util.AttackType;
import com.unaig.noway.util.Direction;
import com.unaig.noway.util.GameHelper;

import static com.unaig.noway.util.Constants.*;
import static com.unaig.noway.util.Direction.*;

public class IceSpell extends Spell{

	public static final String TAG = IceSpell.class.getName();

	private static final Pool<IceSpell> iceSpellPool = new Pool<IceSpell>() {
		@Override
		protected IceSpell newObject() {
			return new IceSpell();
		}
	};

	public static void create(PoolEngine pool, Player player, AttackType type) {
		IceSpell spell = iceSpellPool.obtain();
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
		if((vel.x<0 && vel.y==0) || (vel.x==0 && vel.y==0 && lastDir== LEFT)){
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_LEFT);
		}else if((vel.x>0 && vel.y==0) || (vel.x==0 && vel.y==0 && lastDir==RIGHT)) {
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_RIGHT);
		}else if((vel.x==0 && vel.y>0) || (vel.x==0 && vel.y==0 && lastDir==UP)) {
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_UP);
		}else if((vel.x==0 && vel.y<0) || (vel.x==0 && vel.y==0 && lastDir==DOWN)) {
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_DOWN);
		}else if(vel.x<0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_LEFT_UP);
		}else if(vel.x<0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_LEFT_DOWN);
		}else if(vel.x>0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_RIGHT_UP);
		}else if(vel.x>0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION, ICE_SPELL_RIGHT_DOWN);

		}
	}

	private void strongAttackAnimations() {
		if((vel.x<0 && vel.y==0) || (vel.x==0 && vel.y==0 && lastDir== LEFT)){
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_LEFT);
		}else if((vel.x>0 && vel.y==0) || (vel.x==0 && vel.y==0 && lastDir==RIGHT)) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_RIGHT);
		}else if((vel.x==0 && vel.y>0) || (vel.x==0 && vel.y==0 && lastDir==UP)) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_UP);
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_DOWN);
		}else if(vel.x<0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_LEFT_UP);
		}else if(vel.x<0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_LEFT_DOWN);
		}else if(vel.x>0 && vel.y>0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_RIGHT_UP);
		}else if(vel.x>0 && vel.y<0) {
			animation = GameHelper.setAnimation(FRAME_DURATION_STRONG, ICE2_SPELL_RIGHT_DOWN);
		}
		
	}
	@Override
	public void release() {
		iceSpellPool.free(this);
	}
	
}
