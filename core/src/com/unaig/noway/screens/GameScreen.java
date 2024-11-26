package com.unaig.noway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.tommyettinger.textra.TypingLabel;
import com.unaig.noway.NoWayLabyrinth;
import com.unaig.noway.data.Assets;
import com.unaig.noway.engines.PoolEngine;
import com.unaig.noway.entities.Player;
import com.unaig.noway.entities.enemies.Enemy;
import com.unaig.noway.entities.enemies.Enemy.EnemyListener;
import com.unaig.noway.entities.enemies.GhostEnemy;
import com.unaig.noway.entities.enemies.SpiderEnemy;
import com.unaig.noway.entities.enemies.ZombieEnemy;
import com.unaig.noway.entities.objects.Dialog;
import com.unaig.noway.entities.objects.Object;
import com.unaig.noway.entities.objects.*;
import com.unaig.noway.entities.spells.Spell;
import com.unaig.noway.util.GameHelper;
import com.unaig.noway.util.ImageAnimation;
import de.eskalon.commons.screen.ManagedScreen;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP_PINGPONG;
import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL;
import static com.unaig.noway.entities.Player.*;
import static com.unaig.noway.util.Constants.GAME_UI_JSON;
import static com.unaig.noway.util.Constants.TILE_SIZE;
import static com.unaig.noway.util.ElementType.FIRE;

public class GameScreen extends ManagedScreen implements EnemyListener {
    public static final String TAG = GameScreen.class.getName();

    private final NoWayLabyrinth game;
    private OrthogonalTiledMapRenderer renderer;
    private Stage stage;
    private final SpriteBatch batch;
    private Viewport viewport;
    private ShapeDrawer shaper;
    private static final float CAM_SPEED = 5f;
    public static final float TIME_DISABLED = .05f;
    public static final float FADE_DURATION = .25f;
    public static final float SPELL_FRAME_DURATION = .15f;
    private float levelLabelTime;
    private TypingLabel levelLabel;
    private static Player player;
    private ProgressBar playerHPUI;
    private ProgressBar playerMPUI;

    private PoolEngine poolEngine;

    private ImageAnimation fireTypeAnim;
    private ImageAnimation iceTypeAnim;
    private Button fireTypeIcon;
    private Button fireSpellIcon;
    private Button fire2SpellIcon;
    private Label currentPotionLabel;
    private Button changeElementIcon;
    private float changeTimeDisabled;
    private float potionLeftTimeDisabled;
    private float potionRightTimeDisabled;
    private float currentPotionTimeDisabled;

    private Button iceSpellIcon;
    private Button ice2SpellIcon;
    private Button hpPotionIcon;
    private Button mpPotionIcon;
    private Button arPotionIcon;
    private Label potionLabel;
    private Button changePotionLeft;
    private Button changePotionRight;

    private Button keyIcon;
    private Label keyLabel;

    private boolean canPlayerInteract;
    private Window window;
    private boolean isWindowActive;

    private Array<Object> objects;
    private Object object;
    private int currentPotion;
    private boolean openModal;
    private float gameTime;
    private int enemiesKilled;
    private int chestsOpened;
    public static boolean gameOver;
    private float gameOverTime;
    private TypingLabel gameOverLabel;
    private Table fadeOutGroup;

    private Rectangle exitPos;
    public static Rectangle barrierPos;
    private int contOverLabel;
    private int currentLevel;
    private int rndStairs;
    public static boolean debugControl;


    public GameScreen(NoWayLabyrinth game) {
        this.game = game;
        this.batch = game.getBatch();
    }

    @Override
    protected void create() {

    }

    @Override
    public void show() {
        getInputProcessors().clear();
        renderer = new OrthogonalTiledMapRenderer(Assets.instance.labMap);
        viewport = new ExtendViewport(80 * TILE_SIZE, 80 * TILE_SIZE);
        stage = new Stage(new StretchViewport(1280, 720));
        poolEngine = new PoolEngine();
        canPlayerInteract = false;
        if (pushParams != null) {
            for (int i = 0; i < pushParams.length; i++) {
                switch (i) {
                    case 0:
                        gameTime = (float) pushParams[i];
                        break;
                    case 1:
                        enemiesKilled = (int) pushParams[i];
                        break;
                    case 2:
                        chestsOpened = (int) pushParams[i];
                        break;
                    case 3:
                        currentLevel = (int) pushParams[i];
                        break;
                    case 4:
                        Player currPlayer = (Player) pushParams[i];
                        currPlayer.getItems().get(3).clear();
                        Array<Array<Item>> items = currPlayer.getItems();
                        float hp = currPlayer.getHp();
                        player.init();
                        player.setItems(items);
                        player.setHp(hp);
                        break;
                }
            }
        } else {
            gameTime = 0;
            enemiesKilled = 0;
            chestsOpened = 0;
            currentLevel = 1;
            player = new Player(poolEngine);
        }
        addInputProcessor(stage);
        addInputProcessor(player);
        ((OrthographicCamera) viewport.getCamera()).zoom = 1 / 5f;
        shaper = new ShapeDrawer(batch, Assets.instance.playerAtlas.findRegion("whitePixel"));
        viewport.getCamera().position.set(new Vector3(player.getPos(), 0));
        objects = new Array<>();
        object = null;
        openModal = false;
        gameOverTime = 0;
        gameOver = false;
        gameOverLabel = new TypingLabel();
        levelLabelTime = 3f;
        contOverLabel = 0;
        Chest.keyCount = 0;
        Chest.emptyCount = 0;
        rndStairs = MathUtils.random(1);
        initializeUI();
        initObjects();
        spawnEnemies();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debugControl = false;
    }

    @Override
    public void hide() {
    }

    @Override
    public Color getClearColor() {
        return new Color(.5f, .5f, .7f, 1);
    }

    @Override
    public void render(float delta) {
        update(delta);
        stage.act();
        renderer.setView((OrthographicCamera) viewport.getCamera());
        renderer.render();
        batch.begin();
        renderEntities(delta);
        //Debugging
        // shaper.rectangle(player.getBounds());
        // for (Spell s : poolEngine.spells) {
        //     shaper.rectangle(s.getBounds());
        // }
        // for (Enemy e : poolEngine.enemies) {
        //     shaper.rectangle(e.getBounds());
        // }
        renderUI(delta);
        batch.end();
        stage.draw();

        //DebugPowers
        if (Gdx.input.isKeyJustPressed(Keys.UP)) {
            ((OrthographicCamera) viewport.getCamera()).zoom -= 0.15;
        } else if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
            ((OrthographicCamera) viewport.getCamera()).zoom += 0.15;
        }
        if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
            debugControl = !debugControl;
        }
        if (Gdx.input.isKeyJustPressed(Keys.CONTROL_RIGHT)) {
            if (!game.getScreenManager().inTransition()) {
                game.getScreenManager().pushScreen("Reset", null);
                currentLevel++;
                game.getScreenManager().pushScreen("Game", "blend", 0f, 0, 0, currentLevel);
            }
        }
        GameHelper.resizeGameWindow();
//        Gdx.app.log(TAG, "" + Gdx.graphics.getFramesPerSecond());
        //End debugging

    }

    private void drawHelperKey() {
        Vector2 pos = player.getPos();
        Vector2 size = player.getSize();
        GameHelper.drawEntity(batch, Assets.instance.objectsAtlas.findRegion("rKey"), new Vector2(pos.x + size.x / 5.5f, pos.y + size.y), new Vector2(size.x / 1.5f, size.y / 1.5f));
    }

    private void update(float delta) {
        viewport.apply();
        stage.getViewport().apply();
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport.getCamera().position.lerp(new Vector3(player.getPos(), 0), CAM_SPEED * delta);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        gameTime += delta;
        setElementIconPositions(delta);
        updateLabelPositions();
        canPlayerInteract = checkNearItem();
        isGameOver();
        levelLabelTime -= delta;
        if (levelLabelTime <= 0) {
            levelLabel.addAction(Actions.fadeOut(.75f));
        }
    }

    private void isGameOver() {
        if (gameOver && contOverLabel < 1) {
            Table table = new Table();
            table.setFillParent(true);
            table.align(Align.top);
            if (player.isDead()) {
                gameOverLabel = new TypingLabel("{SHRINK=1.0;1.0;false}{CROWD}{COLOR=SCARLET}GAME OVER", Assets.instance.mainSkin, "title");
            }
            table.padTop(200);
            table.add(gameOverLabel);
            gameOverTime = gameTime;
            contOverLabel++;
            fadeOutGroup.addAction(Actions.fadeOut(1f));
            stage.addActor(table);
        } else if (player.isDead() && gameOver && !openModal) {
            if (gameTime > gameOverTime + 3f) {
                gameOverLabel.addAction(Actions.fadeOut(.25f));
                dieDialog();
            }
        } else if (!player.isDead() && gameOver && !openModal) {
            if (gameTime > gameOverTime + 3f) {
                gameOverLabel.addAction(Actions.fadeOut(.25f));
                winDialog();
            }
        }
    }

    private void dieDialog() {
        new com.badlogic.gdx.scenes.scene2d.ui.Dialog("GAME OVER", Assets.instance.mainSkin) {
            {
                String sTitles = "Time alive: \n\nMonsters defeated: \n\nchests opened: ";
                String sStats = (int) gameOverTime / 60 + " mins " + (int) gameOverTime % 60 + " secs\n\n" + enemiesKilled + "\n\n"
                        + chestsOpened;
                getTitleLabel().setAlignment(Align.center);
                getContentTable().defaults().padLeft(20).padRight(20);
                text("\nYou have been defeated,\n\nYour progress will be reset.\n\n\nStats:");
                getContentTable().row();
                text(sTitles);
                text(sStats);
                button("Back to Menu", false);
                button("Restart", true);
            }

            protected void result(java.lang.Object object) {
                if ((boolean) object) {
                    if (!game.getScreenManager().inTransition()) {
                        game.getScreenManager().pushScreen("Reset", null);
                        game.getScreenManager().pushScreen("Game", "blend");
                    }
                }
            }
        }.show(stage);
        openModal = true;
    }

    private void updateLabelPositions() {
        Vector2 v = hpPotionIcon.localToStageCoordinates(new Vector2(hpPotionIcon.getX(), hpPotionIcon.getY()));
        potionLabel.setPosition(v.x + hpPotionIcon.getWidth() / 2.75f, v.y + hpPotionIcon.getHeight() / 3.5f);
        v = keyIcon.localToStageCoordinates(new Vector2(keyIcon.getX(), keyIcon.getY()));
        keyLabel.setPosition(v.x + 30, stage.getHeight() * .84f);
        potionLabel.setText("x" + player.getItems().get(currentPotion).size);
        keyLabel.setText("x" + player.getItems().get(3).size);
    }

    private void checkObjectInteraction() {
        if (canPlayerInteract && !isWindowActive) {
            if (object instanceof Dialog) {
                resizeObjectWindow("Dialog");
                window.add(object.getLabel());
                object.getLabel().restart();
            } else if (object instanceof Chest) {
                resizeObjectWindow("Chest");
                Chest chest = (Chest) object;
                canPlayerInteract = false;
                boolean isMaxPot = false;
                if (chest.getItemImage() != null) {
                    window.add(chest.getItemImage()).pad(20).padRight(30).padTop(25);
                    if (chest.getItem() instanceof HealthPotion) {
                        if (player.getItems().get(0).size < MAX_POTS) {
                            player.getItems().get(0).add(chest.getItem());
                        } else {
                            isMaxPot = true;
                        }
                    } else if (chest.getItem() instanceof ManaPotion) {
                        if (player.getItems().get(1).size < MAX_POTS) {
                            player.getItems().get(1).add(chest.getItem());
                        } else {
                            isMaxPot = true;
                        }
                    } else if (chest.getItem() instanceof ArmorPotion) {
                        if (player.getItems().get(2).size < MAX_POTS) {
                            player.getItems().get(2).add(chest.getItem());
                        } else {
                            isMaxPot = true;
                        }
                    } else if (chest.getItem() instanceof LabyKey) {
                        if (player.getItems().get(3).size < MAX_POTS) {
                            player.getItems().get(3).add(chest.getItem());
                        } else {
                            isMaxPot = true;
                        }
                    }
                    if (!isMaxPot || chest.getItem() instanceof LabyKey || chest.getItemImage() == null) {
                        chest.setOpen(true);
                        chestsOpened++;
                        window.add(chest.getLabel()).padRight(20).padTop(5);
                        chest.getLabel().restart();
                    } else {
                        window.add(chest.getEmptyLabel()).padRight(20).padTop(5);
                        chest.getEmptyLabel().restart();
                    }
                } else {
                    chest.setOpen(true);
                    chestsOpened++;
                    window.add(chest.getLabel()).padRight(20).padTop(5);
                    chest.getLabel().restart();
                }
            }
            if (onEnabledExit()) {
                if (!gameOver) {
                    Table table = new Table();
                    table.setFillParent(true);
                    table.align(Align.top);
                    if (currentLevel < 5)
                        gameOverLabel = new TypingLabel("{SHRINK=1.0;1.0;false}{CROWD}{COLOR=LIME}LEVEL COMPLETED", Assets.instance.mainSkin, "title");
                    else
                        gameOverLabel = new TypingLabel("{SHRINK=1.0;1.0;false}{CROWD}{COLOR=LIME}[%150]EXIT FOUND", Assets.instance.mainSkin, "title");

                    table.padTop(200);
                    table.add(gameOverLabel);
                    gameOver = true;
                    gameOverTime = gameTime;
                    fadeOutGroup.addAction(Actions.fadeOut(1f));
                    stage.addActor(table);
                }
            }
            if (!gameOver) {
                window.setVisible(true);
                isWindowActive = true;
            }
        } else if (isWindowActive && object instanceof Dialog) {
            object.getLabel().skipToTheEnd();
        }

    }

    private void winDialog() {
        if (currentLevel < 5) {
            nextLevelDialog();
        } else {
            endDialog();
        }

        openModal = true;
    }

    private void endDialog() {
        new com.badlogic.gdx.scenes.scene2d.ui.Dialog("ESCAPE SUCCESSFUL", Assets.instance.mainSkin) {
            {
                String sTitles = "\nRun time: \n\nMonsters defeated: \n\nChests opened: ";
                String sStats = "\n" + (int) gameOverTime / 60 + " mins " + (int) gameOverTime % 60 + " secs\n\n" + enemiesKilled + "\n\n"
                        + chestsOpened;
                getTitleLabel().setAlignment(Align.center);
                getContentTable().defaults().padLeft(20).padRight(20);
                text("\nYou escaped from the labyrinth!\n\nCongratulations on your hard work\nEscaping from this Hell\n\nWant to play again?\n\n\nStats:");
                getContentTable().row();
                text(sTitles);
                text(sStats);
                button("No, Back to menu", false);
                button("Yes, Play again", true);
            }

            protected void result(java.lang.Object object) {
                if ((boolean) object) {
                    if (!game.getScreenManager().inTransition()) {
                        game.getScreenManager().pushScreen("Reset", null);
                        currentLevel++;
                        game.getScreenManager().pushScreen("Game", "blend");
                    }
                }
            }
        }.show(stage);
    }

    private void nextLevelDialog() {
        new com.badlogic.gdx.scenes.scene2d.ui.Dialog("LEVEL COMPLETED", Assets.instance.mainSkin) {
            {
                getTitleLabel().setAlignment(Align.center);
                getContentTable().defaults().padLeft(20).padRight(20);
                text("\nYou escaped the level!\n\n\nContinue to the next layer to keep searching the exit.\n\n\nDifficulty will be increased.");
                getContentTable().row();
                button("Back to Menu", false);
                button("Next level", true);
            }

            protected void result(java.lang.Object object) {
                if ((boolean) object) {
                    if (!game.getScreenManager().inTransition()) {
                        game.getScreenManager().pushScreen("Reset", null);
                        currentLevel++;
                        game.getScreenManager().pushScreen("Game", "blend", gameOverTime, enemiesKilled, chestsOpened, currentLevel, player);
                    }
                }
            }
        }.show(stage);
    }

    private void spawnEnemies() {
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Spawns").getObjects();
        int spawnNum = 0;
        Array<MapObject> transObjList = new Array<>();
        for (MapObject o : collisions) {
            transObjList.add(o);
        }
        transObjList.shuffle();
        switch (currentLevel) {
            case 1:
                spawnNum = 55;
                break;
            case 2:
                spawnNum = 75;
                break;
            case 3:
                spawnNum = 95;
                break;
            case 4:
                spawnNum = 110;
                break;
            case 5:
                spawnNum = 134;
                break;
        }
        for (int i = 0; i < spawnNum; i++) {
            int rnd = MathUtils.random(9);
            MapObject mapObject = transObjList.get(i);
            Rectangle pos = ((RectangleMapObject) mapObject).getRectangle();
            if (mapObject.getName().equals("EnemySpawn") && rnd < 4) {
                SpiderEnemy.create(poolEngine, new Vector2(pos.x - TILE_SIZE / 2f, pos.y - TILE_SIZE / 2f), this);
            } else if (mapObject.getName().equals("EnemySpawn") && rnd < 8) {
                ZombieEnemy.create(poolEngine, new Vector2(pos.x - TILE_SIZE / 2f, pos.y - TILE_SIZE / 2f), this);
            } else if (mapObject.getName().equals("EnemySpawn") && rnd >= 8) {
                GhostEnemy.create(poolEngine, new Vector2(pos.x - TILE_SIZE / 2f, pos.y - TILE_SIZE / 2f), this);
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
        fadeOutGroup = stage.getRoot().findActor("UIContainer");
        playerHPUI = stage.getRoot().findActor("PlayerHP");
        playerMPUI = stage.getRoot().findActor("PlayerMP");
        fireSpellIcon = stage.getRoot().findActor("fireSpellIcon");
        fire2SpellIcon = stage.getRoot().findActor("fire2SpellIcon");
        fireTypeIcon = stage.getRoot().findActor("fireTypeIcon");
        iceSpellIcon = stage.getRoot().findActor("iceSpellIcon");
        ice2SpellIcon = stage.getRoot().findActor("ice2SpellIcon");
        hpPotionIcon = stage.getRoot().findActor("healthPotion");
        mpPotionIcon = stage.getRoot().findActor("manaPotion");
        arPotionIcon = stage.getRoot().findActor("armorPotion");
        changePotionLeft = stage.getRoot().findActor("potionLeftIcon");
        changePotionRight = stage.getRoot().findActor("potionRightIcon");
        keyIcon = stage.getRoot().findActor("keyIcon");
        changeElementIcon = stage.getRoot().findActor("changeElementButton");
        currentPotionLabel = stage.getRoot().findActor("currentPotionLabel");
        currentPotionLabel.setColor(Color.WHITE);
        changeTimeDisabled = TIME_DISABLED;
        potionRightTimeDisabled = TIME_DISABLED;
        potionLeftTimeDisabled = TIME_DISABLED;
        currentPotionTimeDisabled = TIME_DISABLED;
        fireTypeAnim = new ImageAnimation();
        iceTypeAnim = new ImageAnimation();
        fireTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("fireTypeIcon"), NORMAL));
        iceTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("iceTypeIcon"), NORMAL));
        window = new Window("", Assets.instance.mainSkin, "special");
        isWindowActive = false;
        window.setVisible(false);
        potionLabel = new Label("", Assets.instance.mainSkin);
        keyLabel = new Label("", Assets.instance.mainSkin);
        potionLabel.setTouchable(Touchable.disabled);
        currentPotion = 0;
        Table table = new Table();
        table.setFillParent(true);
        levelLabel = new TypingLabel("{FADE}LEVEL L-" + currentLevel + "{ENDFADE}", Assets.instance.mainSkin, "title");
        table.add(levelLabel).padBottom(200);
        stage.addActor(iceTypeAnim);
        fadeOutGroup.addActor(iceTypeAnim);
        stage.addActor(fireTypeAnim);
        fadeOutGroup.addActor(fireTypeAnim);
        stage.addActor(window);
        stage.addActor(potionLabel);
        fadeOutGroup.addActor(potionLabel);
        stage.addActor(keyLabel);
        fadeOutGroup.addActor(keyLabel);
        stage.addActor(table);
    }

    private void resizeObjectWindow(String type) {
        if (type.equals("Dialog")) {
            window.setSize(600, 300); // Set the window size
            window.setPosition(stage.getWidth() / 2f - window.getWidth() / 2f, stage.getHeight() / 2f - window.getHeight() / 2f); // Center the window on the screen
        } else if (type.equals("Chest")) {
            window.setSize(300, 170); // Set the window size
            window.setPosition(stage.getWidth() / 2f - window.getWidth() / 2f, stage.getHeight() / 2f - window.getHeight() / 2f); // Center the window on the screen
        }

    }

    private void initObjects() {
        MapObjects collisions = Assets.instance.labMap.getLayers().get("Objects").getObjects();
        for (int i = 0; i < collisions.getCount(); i++) {
            MapObject mapObject = collisions.get(i);
            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                if (mapObject.getName().equals("Dialog")) {
                    objects.add(new Dialog((String) mapObject.getProperties().get("Text"), rectangle));
                } else if (mapObject.getName().equals("Chest")) {
                    String dir = (String) mapObject.getProperties().get("direction");
                    objects.add(new Chest(dir, rectangle));
                } else if (mapObject.getName().equals("Exit")) {
                    exitPos = new Rectangle(rectangle);
                } else if (mapObject.getName().equals("Barrier")) {
                    barrierPos = new Rectangle(rectangle);
                }

            }
        }
        if (Chest.keyCount < Chest.MAX_KEYS) {
            for (Object o : objects) {
                if (o instanceof Chest) {
                    if (((Chest) o).getItem() == null) {
                        ((Chest) o).setItem(new LabyKey());
                        ((Chest) o).setItemImage(((Chest) o).getItem().getItemImage());
                        o.setLabel(((Chest) o).getItem().getLabel());
                        Chest.emptyCount--;
                        Chest.keyCount++;
                        replaceChestTexture((Chest) o);
                        if (Chest.keyCount == Chest.MAX_KEYS) {
                            break;
                        }
                    }
                }
            }
        }
        if (Chest.emptyCount < 1 && Chest.keyCount < Chest.MAX_KEYS) {
            for (Object o : objects) {
                if (o instanceof Chest) {
                    if (((Chest) o).getItem() instanceof ManaPotion) {
                        ((Chest) o).setItem(new LabyKey());
                        Chest.keyCount++;
                        ((Chest) o).setItemImage(((Chest) o).getItem().getItemImage());
                        o.setLabel(((Chest) o).getItem().getLabel());
                        replaceChestTexture((Chest) o);
                        if (Chest.keyCount == Chest.MAX_KEYS) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void replaceChestTexture(Chest c) {
        switch (c.getDir()) {
            case "down":
                c.setAnimation(new Animation<>(Chest.FRAME_DURATION, Assets.instance.objectsAtlas.findRegions("keyChestDown"), NORMAL));
                break;
            case "up":
                c.setAnimation(new Animation<>(Chest.FRAME_DURATION, Assets.instance.objectsAtlas.findRegions("keyChestUp"), NORMAL));
                break;
            case "left":
                c.setAnimation(new Animation<>(Chest.FRAME_DURATION, Assets.instance.objectsAtlas.findRegions("keyChestLeft"), NORMAL));
                break;
            case "right":
                c.setAnimation(new Animation<>(Chest.FRAME_DURATION, Assets.instance.objectsAtlas.findRegions("keyChestRight"), NORMAL));
                break;
        }
    }

    private void setActorListeners() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Keys.SPACE:
                        updateElementIcons();
                        break;
                    case Keys.F:
                        useCurrentPotion();
                        break;
                    case Keys.E:
                        addIndexPotion();
                        break;
                    case Keys.Q:
                        restPotionIndex();
                        break;
                    case Keys.R:
                        checkObjectInteraction();
                        break;
                }
                return false;
            }
        });
        changeElementIcon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateElementIcons();
            }
        });
        changePotionLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                restPotionIndex();

            }
        });

        changePotionRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addIndexPotion();
            }
        });

        hpPotionIcon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                useCurrentPotion();

            }
        });
        mpPotionIcon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                useCurrentPotion();
            }
        });
        arPotionIcon.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                useCurrentPotion();
            }
        });
    }

    private void useCurrentPotion() {
        player.useItem(currentPotion);
        currentPotionLabel.setTouchable(Touchable.enabled);
    }

    private void addIndexPotion() {
        changePotionRight.setDisabled(true);
        currentPotion += 1;
        if (currentPotion > 2) {
            currentPotion = 0;
            hpPotionIcon.setVisible(true);
            mpPotionIcon.setVisible(false);
            arPotionIcon.setVisible(false);

        } else if (currentPotion == 1) {
            hpPotionIcon.setVisible(false);
            mpPotionIcon.setVisible(true);
            arPotionIcon.setVisible(false);
        } else if (currentPotion == 2) {
            hpPotionIcon.setVisible(false);
            mpPotionIcon.setVisible(false);
            arPotionIcon.setVisible(true);
        }

    }

    private void restPotionIndex() {
        changePotionLeft.setDisabled(true);
        currentPotion -= 1;
        if (currentPotion < 0) {
            currentPotion = 2;
            hpPotionIcon.setVisible(false);
            mpPotionIcon.setVisible(false);
            arPotionIcon.setVisible(true);
        } else if (currentPotion == 0) {
            hpPotionIcon.setVisible(true);
            mpPotionIcon.setVisible(false);
            arPotionIcon.setVisible(false);
        } else if (currentPotion == 1) {
            hpPotionIcon.setVisible(false);
            mpPotionIcon.setVisible(true);
            arPotionIcon.setVisible(false);
        }
    }

    private void updateElementIcons() {
        changeElementIcon.setDisabled(true);
        player.changeElement();
        if (player.getElementType() == FIRE) {
            iceTypeAnim.addAction(Actions.fadeOut(FADE_DURATION));
            fireTypeAnim.addAction(Actions.fadeIn(FADE_DURATION));
            iceSpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            fireSpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            ice2SpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            fire2SpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            ice2SpellIcon.setTouchable(Touchable.disabled);
            fire2SpellIcon.setTouchable(Touchable.enabled);
            fireTypeAnim.setAnimation(new Animation<>(SPELL_FRAME_DURATION, Assets.instance.playerAtlas.findRegions("fireTypeIcon"), NORMAL));
        } else {
            fireTypeAnim.addAction(Actions.fadeOut(FADE_DURATION));
            iceTypeAnim.addAction(Actions.fadeIn(FADE_DURATION));
            fireSpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            iceSpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            fire2SpellIcon.addAction(Actions.fadeOut(FADE_DURATION));
            ice2SpellIcon.addAction(Actions.fadeIn(FADE_DURATION));
            fire2SpellIcon.setTouchable(Touchable.disabled);
            ice2SpellIcon.setTouchable(Touchable.enabled);
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
        for (Object o : objects) {
            if (o instanceof Chest) {
                ((Chest) o).draw(batch, delta);
            }
        }
        renderExit();
        poolEngine.renderEnemies(batch, shaper, delta, player, poolEngine.spells);
        poolEngine.renderSpells(batch, delta);
        player.render(batch, delta);
    }

    private void renderExit() {
        if (currentLevel <= 4) {
            if (rndStairs == 0) {
                batch.draw(Assets.instance.objectsAtlas.findRegion("stairs"), exitPos.x, exitPos.y, TILE_SIZE * 2, TILE_SIZE * 2);
            } else {
                batch.draw(Assets.instance.objectsAtlas.findRegion("stairs2"), exitPos.x, exitPos.y, TILE_SIZE * 2, TILE_SIZE * 2);
            }
        } else if (!onMaxKeys() && currentLevel == 5) {
            batch.draw(Assets.instance.objectsAtlas.findRegion("exitClosed"), exitPos.x, exitPos.y, TILE_SIZE * 2, TILE_SIZE * 2);
        } else if (onMaxKeys() && currentLevel == 5) {
            batch.draw(Assets.instance.objectsAtlas.findRegion("exit"), exitPos.x, exitPos.y, TILE_SIZE * 2, TILE_SIZE * 2);
        }
        if (!onMaxKeys()) {
            batch.draw(Assets.instance.objectsAtlas.findRegion("barrier"), barrierPos.x, barrierPos.y, TILE_SIZE, TILE_SIZE);
        }
    }

    public static boolean onMaxKeys() {
        return player.getItems().get(3).size == 5;
    }

    private boolean onEnabledExit() {
        return onMaxKeys() && player.getBounds().overlaps(exitPos);
    }

    private void renderUI(float delta) {
        playerHPUI.setValue(player.getHp());
        playerMPUI.setValue(player.getMp());
        if (changeElementIcon.isDisabled() && changeTimeDisabled >= 0) {
            changeTimeDisabled -= delta;
            if (changeTimeDisabled < 0) {
                changeTimeDisabled = TIME_DISABLED;
                changeElementIcon.setDisabled(false);
            }
        }
        if (changePotionLeft.isDisabled() && potionLeftTimeDisabled >= 0) {
            potionLeftTimeDisabled -= delta;
            if (potionLeftTimeDisabled < 0) {
                potionLeftTimeDisabled = TIME_DISABLED;
                changePotionLeft.setDisabled(false);
            }
        }
        if (changePotionRight.isDisabled() && potionRightTimeDisabled >= 0) {
            potionRightTimeDisabled -= delta;
            if (potionRightTimeDisabled < 0) {
                potionRightTimeDisabled = TIME_DISABLED;
                changePotionRight.setDisabled(false);
            }
        }
        if (currentPotionLabel.isTouchable() && currentPotionTimeDisabled >= 0) {
            currentPotionTimeDisabled -= delta;
            currentPotionLabel.setColor(Color.BLACK);
            if (currentPotionTimeDisabled < 0) {
                currentPotionTimeDisabled = TIME_DISABLED;
                currentPotionLabel.setTouchable(Touchable.disabled);
            }
        } else {
            currentPotionLabel.setColor(Color.WHITE);
        }

        fireSpellIcon.setDisabled(player.getFireCooldown() > 0f || player.getMp() < BASIC_MANA_COST);
        fire2SpellIcon.setDisabled(player.getFire2Cooldown() > 0f || player.getMp() < STRONG_MANA_COST);
        iceSpellIcon.setDisabled(player.getIceCooldown() > 0f || player.getMp() < BASIC_MANA_COST);
        ice2SpellIcon.setDisabled(player.getIce2Cooldown() > 0f || player.getMp() < STRONG_MANA_COST);
        if (canPlayerInteract) {
            drawHelperKey();
        }
    }

    private boolean checkNearItem() {
        for (Object o : objects) {
            if (o instanceof Dialog) {
                if (player.getBounds().overlaps(o.getRectangle())) {
                    if (object == null)
                        object = o;
                    return true;
                }
            } else if (o instanceof Chest) {
                if (((Chest) o).isOpen() && player.getBounds().overlaps(o.getRectangle())) {
                    if (!gameOver)
                        openModal = true;
                    return false;
                } else if (player.getBounds().overlaps(o.getRectangle())) {
                    if (object == null)
                        object = o;
                    return true;
                }
            }
        }
        if (player.getBounds().overlaps(exitPos) && player.getItems().get(3).size == 5) return true;
        if (!openModal) {
            object = null;
            isWindowActive = false;
            window.setVisible(false);
            window.clear();
        }
        if (!gameOver)
            openModal = false;
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
        poolEngine.clear();
        batch.dispose();
        renderer.dispose();
        stage.dispose();
    }

    @Override
    public void onEnemyDead() {
        enemiesKilled++;
    }
}
