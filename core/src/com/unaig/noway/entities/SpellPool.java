package com.unaig.noway.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class SpellPool {

	public Array<Spell> spells;
	
	public SpellPool() {
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
