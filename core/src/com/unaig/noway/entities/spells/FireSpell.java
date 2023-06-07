package com.unaig.noway.entities.spells;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.SpellPool;
import com.unaig.noway.util.AttackType;
import com.unaig.noway.util.Constants;
import com.unaig.noway.util.Direction;

public class FireSpell extends Spell{

	private final float frameDuration = .06f;
	private final float frameDurationStrong = .075f;

	private static Pool<FireSpell> spellPool = Pools.get(FireSpell.class);

	public static void create(SpellPool pool,Player player, AttackType type) {
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
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_LEFT), PlayMode.LOOP);
		}else if((vel.x>0 && vel.y==0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.RIGHT)) {
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_RIGHT), PlayMode.LOOP);
		}else if((vel.x==0 && vel.y>0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.UP)) {
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_UP), PlayMode.LOOP);
		}else if((vel.x==0 && vel.y<0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.DOWN)) {
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_DOWN), PlayMode.LOOP);
		}else if(vel.x<0 && vel.y>0) {
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_LEFT_UP), PlayMode.LOOP);
		}else if(vel.x<0 && vel.y<0) {
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_LEFT_DOWN), PlayMode.LOOP);
		}else if(vel.x>0 && vel.y>0) {
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_RIGHT_UP), PlayMode.LOOP);
		}else if(vel.x>0 && vel.y<0) {
			animation = new Animation<>(frameDuration, Assets.instance.playerAtlas.findRegions(Constants.FIRE_SPELL_RIGHT_DOWN), PlayMode.LOOP);
		}		
	}
	private void strongAttackAnimations() {
		if((vel.x<0 && vel.y==0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.LEFT)){
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_LEFT), PlayMode.LOOP);
		}else if((vel.x>0 && vel.y==0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.RIGHT)) {
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_RIGHT), PlayMode.LOOP);
		}else if((vel.x==0 && vel.y>0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.UP)) {
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_UP), PlayMode.LOOP);
		}else if((vel.x==0 && vel.y<0) || (vel.x==0 && vel.y==0 && playerLastDir==Direction.DOWN)) {
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_DOWN), PlayMode.LOOP);
		}else if(vel.x<0 && vel.y>0) {
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_LEFT_UP), PlayMode.LOOP);
		}else if(vel.x<0 && vel.y<0) {
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_LEFT_DOWN), PlayMode.LOOP);
		}else if(vel.x>0 && vel.y>0) {
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_RIGHT_UP), PlayMode.LOOP);
		}else if(vel.x>0 && vel.y<0) {
			animation = new Animation<>(frameDurationStrong, Assets.instance.playerAtlas.findRegions(Constants.FIRE2_SPELL_RIGHT_DOWN), PlayMode.LOOP);
		}
		
	}
	@Override
	public void release() {
		spellPool.free(this);
	}
	
}
