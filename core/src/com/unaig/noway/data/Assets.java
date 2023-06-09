package com.unaig.noway.data;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Assets {

	public static final String TAG = Assets.class.getName();
	
	//Singleton
	public static final Assets instance= new Assets();
	private Assets() {}
	
	public TiledMap labMap;
	public TextureAtlas playerAtlas;
	public TextureAtlas enemiesAtlas;

	public void load() {
		playerAtlas= new TextureAtlas("sprites/PlayerAnimations.atlas");
		enemiesAtlas = new TextureAtlas("sprites/EnemiesAnimations.atlas");
		labMap = new TmxMapLoader().load("maps/lvl1.tmx");
	}
	
	public void dispose() {
		labMap.dispose();
		playerAtlas.dispose();
	}
	
}
