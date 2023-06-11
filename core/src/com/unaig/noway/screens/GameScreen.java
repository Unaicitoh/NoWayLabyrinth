package com.unaig.noway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.unaig.noway.data.Assets;
import com.unaig.noway.entities.Player;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.enemies.Enemy;
import com.unaig.noway.entities.spells.Spell;

import static com.unaig.noway.util.Constants.TILE_SIZE;

public class GameScreen extends ScreenAdapter {
	public static final String TAG = GameScreen.class.getName();

	private OrthogonalTiledMapRenderer renderer;
	
	private SpriteBatch batch;
	private Viewport viewport;
	private ShapeRenderer shaper;
	private static final float CAM_SPEED=5f;
	private Player player;
	private Enemy spider;
	private PoolEngine poolEngine;
	@Override
	public void show() {
		renderer = new OrthogonalTiledMapRenderer(Assets.instance.labMap);
		viewport=new ExtendViewport(80* TILE_SIZE, 80* TILE_SIZE);
		batch = (SpriteBatch) renderer.getBatch();
		poolEngine = new PoolEngine();
		player = new Player(poolEngine);
		Enemy.create(poolEngine);
		Gdx.input.setInputProcessor(player);
		((OrthographicCamera)viewport.getCamera()).zoom=1/5f;
		shaper=new ShapeRenderer();
		viewport.getCamera().position.set(new Vector3(player.getPos(),0));
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(.15f,.15f, .15f, 1f);
		viewport.apply();
		viewport.getCamera().position.lerp(new Vector3(player.getPos(),0), CAM_SPEED*delta);
		shaper.setProjectionMatrix(viewport.getCamera().combined);
		
		renderer.setView((OrthographicCamera) viewport.getCamera());
		renderer.render();
		
		batch.begin();
		poolEngine.renderSpells(batch, delta);
		player.render((SpriteBatch) renderer.getBatch(),delta);
		poolEngine.renderEnemies(batch, delta, player);

		batch.end();
		
		shaper.begin(ShapeType.Line);
		shaper.rect(player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);
		for(Spell s: poolEngine.spells) {
			shaper.rect(s.getSpellBounds().x, s.getSpellBounds().y, s.getSpellBounds().width, s.getSpellBounds().height);

		}
//		for(Rectangle r: GameHelper.rects) {
//			shaper.rect(r.x,r.y,r.width,r.height);
//		}
//		for(Polygon p: GameHelper.polys) {
//			shaper.polygon(p.getTransformedVertices());
//		}
		shaper.end();
		
		if(Gdx.input.isKeyJustPressed(Keys.UP)) {
			((OrthographicCamera)viewport.getCamera()).zoom-=0.15;
		}else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
			((OrthographicCamera)viewport.getCamera()).zoom+=0.15;
		}
		if(Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			player.maxVel+= TILE_SIZE;
		}else if(Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			player.maxVel-= TILE_SIZE;
		}
		
//		Gdx.app.log(TAG, ""+Gdx.graphics.getFramesPerSecond());

	}
		

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);

	}


	@Override
	public void dispose() {
		poolEngine.clear();
		batch.dispose();
		shaper.dispose();
		renderer.dispose();
	}
}
