package com.unaig.noway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.unaig.noway.data.Assets;
import com.unaig.noway.screens.GameScreen;
import de.eskalon.commons.core.ManagedGame;
import de.eskalon.commons.screen.ManagedScreen;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.*;

public class NoWayLabyrinth extends ManagedGame<ManagedScreen, ScreenTransition> {


    private SpriteBatch batch;

    @Override
    public void create() {
        super.create();
        batch = new SpriteBatch();
        Assets.instance.load();
        screenManager.addScreen("Game",new GameScreen(this));
        BlendingTransition b = new BlendingTransition(batch, 1f);
        screenManager.addScreenTransition("blend", b);
        screenManager.pushScreen("Game","blend");
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        Assets.instance.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
