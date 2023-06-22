package com.unaig.noway.data;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;

public class Assets {

    //Singleton
    public static final Assets instance = new Assets();
    public static final String PLAYER_ANIMATIONS_ATLAS = "sprites/PlayerAnimations.atlas";
    public static final String ENEMIES_ANIMATIONS_ATLAS = "sprites/EnemiesAnimations.atlas";
    public static final String OBJECTS_ANIMATIONS_ATLAS = "sprites/ObjectsAtlas.atlas";

    public static final String MAIN_SKIN_JSON = "skins/main-skin.json";
    public static final String MAP_LVL_1 = "maps/lvl1.tmx";

    private Assets() {
    }

    private AssetManager assets;
    public SceneComposerStageBuilder sceneBuilder;
    public TiledMap labMap;
    public TextureAtlas playerAtlas;
    public TextureAtlas enemiesAtlas;
    public TextureAtlas objectsAtlas;

    public Skin mainSkin;

    public void load() {
        assets = new AssetManager();
        labMap = new TmxMapLoader().load(MAP_LVL_1);
        sceneBuilder = new SceneComposerStageBuilder();
        //Loading
        assets.load(PLAYER_ANIMATIONS_ATLAS, TextureAtlas.class);
        assets.load(ENEMIES_ANIMATIONS_ATLAS, TextureAtlas.class);
        assets.load(OBJECTS_ANIMATIONS_ATLAS, TextureAtlas.class);
        assets.load(MAIN_SKIN_JSON, Skin.class);
        assets.finishLoading();
        //Getting
        playerAtlas = assets.get(PLAYER_ANIMATIONS_ATLAS);
        enemiesAtlas = assets.get(ENEMIES_ANIMATIONS_ATLAS);
        objectsAtlas = assets.get(OBJECTS_ANIMATIONS_ATLAS);
        mainSkin = assets.get(MAIN_SKIN_JSON);

    }

    public void dispose() {
        labMap.dispose();
        playerAtlas.dispose();
        enemiesAtlas.dispose();
        objectsAtlas.dispose();
        mainSkin.dispose();
        assets.dispose();
    }

}
