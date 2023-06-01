package com.unaig.noway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.Player;

public class GameScreen extends ScreenAdapter {
	public static final String TAG = GameScreen.class.getName();

	private OrthogonalTiledMapRenderer renderer;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private static final float CAM_SPEED=4f;
	private Player player;
	@Override
	public void show() {
		renderer = new OrthogonalTiledMapRenderer(Assets.instance.labMap);
		camera = new OrthographicCamera();
		batch = (SpriteBatch) renderer.getBatch();
		player = new Player(Assets.instance.playerAtlas.createSprite("runDown", 0));
		Gdx.input.setInputProcessor(player);
		camera.zoom=1/4f;

	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(.15f,.15f, .15f, 1f);
		camera.position.lerp(new Vector3(player.getPos(), 0), CAM_SPEED*delta);
		camera.update();
		renderer.setView(camera);
		renderer.render();
		batch.begin();
		player.render((SpriteBatch) renderer.getBatch());
		batch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth=width;
		camera.viewportHeight=height;
		camera.update();

	}


	@Override
	public void dispose() {
		batch.dispose();
	}
}
