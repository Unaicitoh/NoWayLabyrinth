package com.unaig.noway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.unaig.noway.NoWayLabyrinth;
import com.unaig.noway.data.Assets;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.enemies.Enemy;
import com.unaig.noway.entities.enemies.SpiderEnemy;
import com.unaig.noway.entities.spells.Spell;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.unaig.noway.util.Constants.GAME_UI_JSON;
import static com.unaig.noway.util.Constants.TILE_SIZE;

public class GameScreen extends ScreenAdapter {
    public static final String TAG = GameScreen.class.getName();

    private NoWayLabyrinth game;
    private OrthogonalTiledMapRenderer renderer;
    private Stage stage;
    private SpriteBatch batch;
    private Viewport viewport;
    private ShapeDrawer shaper;
    private static final float CAM_SPEED = 5f;
    private Player player;
    private ProgressBar playerHPUI;
    private ProgressBar playerMPUI;

    private PoolEngine poolEngine;

    public GameScreen(NoWayLabyrinth game) {
        this.game = game;
    }

    @Override
    public void show() {
        renderer = new OrthogonalTiledMapRenderer(Assets.instance.labMap);
        viewport = new ExtendViewport(80 * TILE_SIZE, 80 * TILE_SIZE);
        batch = (SpriteBatch) renderer.getBatch();
        stage = new Stage(new ExtendViewport(960, 640), batch);
        poolEngine = new PoolEngine();
        player = new Player(poolEngine);
        SpiderEnemy.create(poolEngine);
        Gdx.input.setInputProcessor(player);
        ((OrthographicCamera) viewport.getCamera()).zoom = 1 / 5f;
        shaper = new ShapeDrawer(batch, Assets.instance.playerAtlas.findRegion("whitePixel"));
        viewport.getCamera().position.set(new Vector3(player.getPos(), 0));

        //Draw UI
        Assets.instance.sceneBuilder.build(stage, Assets.instance.mainSkin, Gdx.files.internal(GAME_UI_JSON));
        playerHPUI = stage.getRoot().findActor("PlayerHP");
        playerHPUI.setValue(player.getHp());
        playerMPUI = stage.getRoot().findActor("PlayerMP");
        playerMPUI.setValue(player.getMp());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(.15f, .15f, .15f, 1f);
        viewport.apply();
        viewport.getCamera().position.lerp(new Vector3(player.getPos(), 0), CAM_SPEED * delta);
        stage.act();
        renderer.setView((OrthographicCamera) viewport.getCamera());
        renderer.render();

        batch.begin();
        poolEngine.renderSpells(batch, delta);
        player.render(batch, delta);
        poolEngine.renderEnemies(batch, shaper, delta, player, poolEngine.spells);
        playerHPUI.setValue(player.getHp());
        playerMPUI.setValue(player.getMp());

        shaper.rectangle(player.getBounds());
        for (Spell s : poolEngine.spells) {
            shaper.rectangle(s.getBounds());
        }
        for (Enemy e : poolEngine.enemies) {
            shaper.rectangle(e.getBounds());

        }
        batch.end();

        stage.draw();

        if (Gdx.input.isKeyJustPressed(Keys.UP)) {
            ((OrthographicCamera) viewport.getCamera()).zoom -= 0.15;
        } else if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
            ((OrthographicCamera) viewport.getCamera()).zoom += 0.15;
        }
        if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
            player.maxVel += TILE_SIZE;
        } else if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
            player.maxVel -= TILE_SIZE;
        }
        if (Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
            game.setScreen(new GameScreen(game));
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
        renderer.dispose();
        stage.dispose();
    }
}
