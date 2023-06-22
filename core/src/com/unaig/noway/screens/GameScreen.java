package com.unaig.noway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.unaig.noway.NoWayLabyrinth;
import com.unaig.noway.data.Assets;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.enemies.Enemy;
import com.unaig.noway.entities.enemies.GhostEnemy;
import com.unaig.noway.entities.enemies.SpiderEnemy;
import com.unaig.noway.entities.enemies.ZombieEnemy;
import com.unaig.noway.entities.spells.FireSpell;
import com.unaig.noway.entities.spells.IceSpell;
import com.unaig.noway.entities.spells.Spell;
import com.unaig.noway.util.GameHelper;
import com.unaig.noway.util.ImageAnimation;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL;
import static com.unaig.noway.entities.Player.STRONG_ATTACK_COOLDOWN;
import static com.unaig.noway.entities.Player.STRONG_MANA_COST;
import static com.unaig.noway.util.AttackType.STRONG;
import static com.unaig.noway.util.Constants.GAME_UI_JSON;
import static com.unaig.noway.util.Constants.TILE_SIZE;
import static com.unaig.noway.util.ElementType.FIRE;
import static com.unaig.noway.util.ElementType.ICE;

public class GameScreen extends ScreenAdapter {
    public static final String TAG = GameScreen.class.getName();

    private final NoWayLabyrinth game;
    private OrthogonalTiledMapRenderer renderer;
    private Stage stage;
    private SpriteBatch batch;
    private Viewport viewport;
    private ShapeDrawer shaper;
    private static final float CAM_SPEED = 5f;
    public static final float CHANGE_TIME_DISABLED = .05f;
    public static final float FADE_DURATION = .25f;
    public static final float SPELL_FRAME_DURATION = .15f;
    private Player player;
    private ProgressBar playerHPUI;
    private ProgressBar playerMPUI;

    private PoolEngine poolEngine;

    private ImageAnimation fireTypeAnim;
    private ImageAnimation iceTypeAnim;
    private Button fireTypeIcon;
    private Button fireSpellIcon;
    private Button fire2SpellIcon;
    private Button changeElementIcon;
    private float changeTimeDisabled;
    private Button iceSpellIcon;
    private Button ice2SpellIcon;
    private boolean canPlayerInteract;

    public GameScreen(NoWayLabyrinth game) {
        this.game = game;
    }

    @Override
    public void show() {
        renderer = new OrthogonalTiledMapRenderer(Assets.instance.labMap);
        viewport = new ExtendViewport(80 * TILE_SIZE, 80 * TILE_SIZE);
        batch = new SpriteBatch();
        stage = new Stage(new ExtendViewport(1280, 720));
        poolEngine = new PoolEngine();
        player = new Player(poolEngine);
        canPlayerInteract = false;
        spawnEnemies();
        InputMultiplexer im = new InputMultiplexer(stage, player);
        Gdx.input.setInputProcessor(im);
        ((OrthographicCamera) viewport.getCamera()).zoom = 1 / 5f;
        shaper = new ShapeDrawer(batch, Assets.instance.playerAtlas.findRegion("whitePixel"));
        viewport.getCamera().position.set(new Vector3(player.getPos(), 0));
        initializeUI();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(.15f, .15f, .15f, 1f);
        viewport.apply();
        viewport.getCamera().position.lerp(new Vector3(player.getPos(), 0), CAM_SPEED * delta);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        update();
        renderer.setView((OrthographicCamera) viewport.getCamera());
        renderer.render();
        batch.begin();
        renderEntities(delta);
        //Debugging
        shaper.rectangle(player.getBounds());
        for (Spell s : poolEngine.spells) {
            shaper.rectangle(s.getBounds());
        }
        for (Enemy e : poolEngine.enemies) {
            shaper.rectangle(e.getBounds());
//            for(Rectangle r : e.lineSight){
//                shaper.rectangle(r);
//                e.lineSight.removeValue(r,true);
//            }

        }
        if (canPlayerInteract) {
            Gdx.app.log(TAG, "prompting key");
            Vector2 pos = player.getPos();
            Vector2 size = player.getSize();
            GameHelper.drawEntity(batch, Assets.instance.objectsAtlas.findRegion("rKey"), new Vector2(pos.x + size.x / 5.5f, pos.y + size.y), new Vector2(size.x / 1.5f, size.y / 1.5f));
        }
        batch.end();
        renderUI(delta);
        stage.act();
        setElementIconPositions(delta);
        stage.draw();
        //DebugPowers
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
        //End debugging
//		Gdx.app.log(TAG, ""+Gdx.graphics.getFramesPerSecond());

    }

    private void update() {
        canPlayerInteract = checkNearItem();
    }

    private void spawnEnemies() {
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Spawns").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            int rnd = MathUtils.random(9);
            MapObject mapObject = collisions.get(i);
            Rectangle pos = ((RectangleMapObject) mapObject).getRectangle();
            if (mapObject.getName().equals("EnemySpawn") && rnd < 4) {
                SpiderEnemy.create(poolEngine, new Vector2(pos.x - TILE_SIZE / 2f, pos.y - TILE_SIZE / 2f));
            } else if (mapObject.getName().equals("EnemySpawn") && rnd < 8) {
                ZombieEnemy.create(poolEngine, new Vector2(pos.x - TILE_SIZE / 2f, pos.y - TILE_SIZE / 2f));
            } else if (mapObject.getName().equals("EnemySpawn") && rnd >= 8) {
                GhostEnemy.create(poolEngine, new Vector2(pos.x - TILE_SIZE / 2f, pos.y - TILE_SIZE / 2f));
            }
        }
    }

    private void initializeUI() {
        Assets.instance.sceneBuilder.build(stage, Assets.instance.mainSkin, Gdx.files.internal(GAME_UI_JSON));
        findActors();
        playerHPUI.setValue(player.getHp());
        playerMPUI.setValue(player.getMp());
        setActorListeners();
    }

    private void findActors() {
        playerHPUI = stage.getRoot().findActor("PlayerHP");
        playerMPUI = stage.getRoot().findActor("PlayerMP");
        fireSpellIcon = stage.getRoot().findActor("fireSpellIcon");
        fire2SpellIcon = stage.getRoot().findActor("fire2SpellIcon");
        fireTypeIcon = stage.getRoot().findActor("fireTypeIcon");
        iceSpellIcon = stage.getRoot().findActor("iceSpellIcon");
        ice2SpellIcon = stage.getRoot().findActor("ice2SpellIcon");
        changeElementIcon = stage.getRoot().findActor("changeElementButton");
        changeTimeDisabled = CHANGE_TIME_DISABLED;
        fireTypeAnim = new ImageAnimation();
        iceTypeAnim = new ImageAnimation();
        fireTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("fireTypeIcon"), NORMAL));
        iceTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("iceTypeIcon"), NORMAL));
        stage.addActor(iceTypeAnim);
        stage.addActor(fireTypeAnim);
    }

    private void setActorListeners() {
        changeElementIcon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateElementActions();
            }
        });
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.SPACE) {
                    updateElementActions();
                }
                return false;
            }
        });
        fire2SpellIcon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (player.getElementType() == FIRE && player.getFire2Cooldown() <= 0f && player.getMp() - STRONG_MANA_COST >= 0) {
                    player.setMp(player.getMp() - STRONG_MANA_COST);
                    player.setStateTime(0);
                    player.setFire2Cooldown(STRONG_ATTACK_COOLDOWN);
                    FireSpell.create(poolEngine, player, STRONG);
                }
            }
        });

        ice2SpellIcon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (player.getElementType() == ICE && player.getIce2Cooldown() <= 0f && player.getMp() - STRONG_MANA_COST >= 0) {
                    player.setMp(player.getMp() - STRONG_MANA_COST);
                    player.setStateTime(0);
                    player.setIce2Cooldown(STRONG_ATTACK_COOLDOWN);
                    IceSpell.create(poolEngine, player, STRONG);
                }
            }
        });
    }

    private void updateElementActions() {
        changeElementIcon.setDisabled(true);
        player.changeElement();
        if (player.getElementType() == FIRE) {
            iceTypeAnim.addAction(Actions.fadeOut(FADE_DURATION));
            fireTypeAnim.addAction(Actions.fadeIn(FADE_DURATION));
            iceSpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            fireSpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            ice2SpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            fire2SpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            fireTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("fireTypeIcon"), NORMAL));
        } else {
            fireTypeAnim.addAction(Actions.fadeOut(FADE_DURATION));
            iceTypeAnim.addAction(Actions.fadeIn(FADE_DURATION));
            fireSpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            iceSpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            fire2SpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            ice2SpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            iceTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("iceTypeIcon"), NORMAL));

        }
        fireTypeAnim.setTime(0);
        iceTypeAnim.setTime(0);
    }


    private void setElementIconPositions(float delta) {
        fireTypeAnim.act(delta);
        if (fireTypeAnim.getAnimation().isAnimationFinished(fireTypeAnim.getTime())) {
            fireTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("fire2TypeIcon"), LOOP_PINGPONG));
            fireTypeAnim.setTime(0);
        }
        fireTypeAnim.setPose(fireTypeAnim.getAnimation().getKeyFrame(fireTypeAnim.getTime()));
        Vector2 pos = new Vector2(fireTypeIcon.localToStageCoordinates(new Vector2(fireTypeIcon.getX(), fireTypeIcon.getY())));
        fireTypeAnim.setPosition(pos.x, pos.y);

        iceTypeAnim.act(delta);
        if (iceTypeAnim.getAnimation().isAnimationFinished(fireTypeAnim.getTime())) {
            iceTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("ice2TypeIcon"), LOOP_PINGPONG));
            iceTypeAnim.setTime(0);
        }
        iceTypeAnim.setPose(iceTypeAnim.getAnimation().getKeyFrame(iceTypeAnim.getTime()));
        pos = new Vector2(fireTypeIcon.localToStageCoordinates(new Vector2(fireTypeIcon.getX(), fireTypeIcon.getY())));
        iceTypeAnim.setPosition(pos.x, pos.y);

    }

    private void renderEntities(float delta) {
        poolEngine.renderSpells(batch, delta);
        poolEngine.renderEnemies(batch, shaper, delta, player, poolEngine.spells);
        player.render(batch, delta);
    }

    private void renderUI(float delta) {
        playerHPUI.setValue(player.getHp());
        playerMPUI.setValue(player.getMp());
        if (changeElementIcon.isDisabled() && changeTimeDisabled >= 0) {
            changeTimeDisabled -= delta;
            if (changeTimeDisabled < 0) {
                changeTimeDisabled = CHANGE_TIME_DISABLED;
                changeElementIcon.setDisabled(false);
            }
        }
        fireSpellIcon.setDisabled(player.getFireCooldown() > 0f);
        fire2SpellIcon.setDisabled(player.getFire2Cooldown() > 0f);
        iceSpellIcon.setDisabled(player.getIceCooldown() > 0f);
        ice2SpellIcon.setDisabled(player.getIce2Cooldown() > 0f);
    }

    private boolean checkNearItem() {
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Objects").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            MapObject mapObject = collisions.get(i);
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                if (player.getBounds().overlaps(rectangle)) {
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height);
        setElementIconPositions(Gdx.graphics.getDeltaTime());

    }


    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        poolEngine.clear();
        batch.dispose();
        renderer.dispose();
        stage.dispose();
    }
}
