package com.unaig.noway.engines;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.enemies.Enemy;
import com.unaig.noway.entities.spells.Spell;

public class PoolEngine {

	public Array<Spell> spells;
	public Array<Enemy> enemies;

	public PoolEngine() {
		spells=new Array<>();
		enemies= new Array<>();
	}
	
	public void add(Spell spell) {
		spells.add(spell);
	}

	public void add(Enemy enemy) {
		enemies.add(enemy);
	}
	public void renderSpells(SpriteBatch batch, float delta) {
		for(Spell s: spells) {
			if(!s.isAlive) {
				s.reset();
				s.release();
				spells.removeValue(s, true);
				continue;
			}
			s.render(batch,delta);
		}

	}
	public void renderEnemies(SpriteBatch batch, float delta, Player player){
		for(Enemy e: enemies){
			if(!e.isAlive) {
				e.reset();
				e.release();
				enemies.removeValue(e, true);
				continue;
			}
			e.render(batch,delta,player);
		}
	}
	
	public void clear() {
		spells.clear();
	}
}
