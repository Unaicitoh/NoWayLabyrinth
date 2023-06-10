package com.unaig.noway.engines;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.unaig.noway.entities.spells.Spell;

public class PoolEngine {

	public Array<Spell> spells;
	
	public PoolEngine() {
		spells=new Array<>();
	}
	
	public void add(Spell spell) {
		spells.add(spell);
	}
	
	
	public void render(SpriteBatch batch, float delta) {
		for(Spell s: spells) {
			if(!s.isAlive) {
				s.reset();
				s.release();
				spells.removeValue(s, false);
				continue;
			}
			s.draw(batch,delta);
		}
	}
	
	public void clear() {
		spells.clear();
	}
}
